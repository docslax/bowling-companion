package ca.josephroque.bowlingcompanion.series

import android.content.Context
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.view.*
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.games.GameControllerFragment
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.settings.Settings
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of series.
 */
class SeriesListFragment : ListFragment<Series, SeriesRecyclerViewAdapter>(),
        SeriesDialog.OnSeriesDialogInteractionListener,
        ListFragment.OnListFragmentInteractionListener,
        IFloatingActionButtonHandler
{

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "SeriesListFragment"

        /** Identifier for the argument that represents the [League] whose series are displayed. */
        private const val ARG_LEAGUE = "${TAG}_league"

        /** Identifier for the single select mode. */
        private const val ARG_SINGLE_SELECT_MODE = "${TAG}_single_select"

        /**
         * Create a new instance
         *
         * @param league the league whose series will be listed
         * @return the new instance
         */
        fun newInstance(league: League, singleSelectMode: Boolean = false): SeriesListFragment {
            val fragment = SeriesListFragment()
            fragment.canIgnoreListener = true
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_LEAGUE, league)
                putBoolean(ARG_SINGLE_SELECT_MODE, singleSelectMode)
            }
            return fragment
        }
    }

    /** The league whose series are to be displayed. */
    private var league: League? = null

    /** When true, the features of the fragment are limited to only offer selecting a single series. */
    private var singleSelectMode: Boolean = false

    /** Indicates how to render series in the list. */
    private var seriesView: Series.Companion.View = Series.Companion.View.Expanded
        set(value) {
            context?.let {
                PreferenceManager.getDefaultSharedPreferences(it)
                        .edit()
                        .putInt(Series.PREFERRED_VIEW, value.ordinal)
                        .apply()
            }

            activity?.invalidateOptionsMenu()
            adapter?.seriesView = value
            field = value
        }

    /** @Override. */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            league = it.getParcelable(ARG_LEAGUE)
            singleSelectMode = it.getBoolean(ARG_SINGLE_SELECT_MODE)
        }
        context?.let {
            seriesView = Series.Companion.View.fromInt(PreferenceManager.getDefaultSharedPreferences(it)
                    .getInt(Series.PREFERRED_VIEW, Series.Companion.View.Expanded.ordinal))!!
        }

        setHasOptionsMenu(!singleSelectMode)
        listener = listener ?: this
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        singleSelectMode = arguments?.getBoolean(ARG_SINGLE_SELECT_MODE) ?: false
        if (singleSelectMode) {
            val parent = parentFragment as? OnListFragmentInteractionListener ?: return
            listener = parent
        }
    }

    /** @Override. */
    override fun onResume() {
        super.onResume()
        val context = context ?: return
        val league = league ?: return
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

        val shouldHighlightSeries = preferenceManager.getBoolean(Settings.HIGHLIGHT_SERIES_ENABLED, true)
        val shouldHighlightScores = preferenceManager.getBoolean(Settings.HIGHLIGHT_SCORE_ENABLED, true)

        adapter?.let {
            it.gameHighlightMin = league.gameHighlight
            it.seriesHighlightMin = league.seriesHighlight
            it.shouldHighlightSeries = shouldHighlightSeries
            it.shouldHighlightScores = shouldHighlightScores
        }
    }

    /** @Override. */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_series, menu)

        when (seriesView) {
            Series.Companion.View.Expanded -> {
                menu.findItem(R.id.action_series_expanded_view).isVisible = false
                menu.findItem(R.id.action_series_condensed_view).isVisible = true
            }
            Series.Companion.View.Condensed -> {
                menu.findItem(R.id.action_series_expanded_view).isVisible = true
                menu.findItem(R.id.action_series_condensed_view).isVisible = false
            }
        }
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_series_condensed_view, R.id.action_series_expanded_view -> {
                val view = if (item.itemId == R.id.action_series_expanded_view) Series.Companion.View.Expanded else Series.Companion.View.Condensed
                seriesView = view
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** @Override */
    override fun buildAdapter(): SeriesRecyclerViewAdapter {
        val adapter = SeriesRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = !singleSelectMode
        adapter.longPressable = !singleSelectMode
        adapter.seriesView = seriesView
        return adapter
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<Series>> {
        return async(CommonPool) {
            this@SeriesListFragment.context?.let { context ->
                league?.let {
                    return@async it.fetchSeries(context).await()
                }
            }

            emptyList<Series>().toMutableList()
        }
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return if (singleSelectMode) {
            R.drawable.ic_add
        } else {
            null
        }
    }

    /** @Override */
    override fun onFabClick() {
        promptAddOrEditSeries()
    }

    /** @Override */
    override fun onFinishSeries(series: Series) {
        refreshList(series)
    }

    /** @Override */
    override fun onDeleteSeries(series: Series) {
        onItemDelete(series)
    }

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is Series) {
            if (longPress) {
                promptAddOrEditSeries(item)
            } else {
                showGameDetails(item)
            }
        }
    }

    /**
     * Display a prompt to add or edit a series.
     *
     * @param series the series to edit, or null if a new series should be added
     */
    private fun promptAddOrEditSeries(series: Series? = null) {
        if (series != null) {
            val newFragment = SeriesDialog.newInstance(series)
            fragmentNavigation?.pushDialogFragment(newFragment)
        } else {
            TODO("not implemented")
        }
    }

    /**
     * Push fragment to show game details of a [Series]
     *
     * @param series the series whose games will be shown
     */
    private fun showGameDetails(series: Series) {
        val newFragment = GameControllerFragment.newInstance(listOf(series))
        fragmentNavigation?.pushFragment(newFragment)
    }
}
