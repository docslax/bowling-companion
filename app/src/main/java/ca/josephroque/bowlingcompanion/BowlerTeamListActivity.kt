package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.FloatingActionButton.OnVisibilityChangedListener
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.bowlers.BowlerFragment
import ca.josephroque.bowlingcompanion.bowlers.BowlerDialog
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.TeamFragment
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.utils.Email
import kotlinx.android.synthetic.main.activity_bowler_team_list.*
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference
import ca.josephroque.bowlingcompanion.settings.SettingsActivity
import android.content.Intent

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to display bowler and team fragments.
 */
class BowlerTeamListActivity : AppCompatActivity(),
        BowlerFragment.OnBowlerFragmentInteractionListener,
        TeamFragment.OnTeamFragmentInteractionListener,
        BowlerDialog.OnBowlerDialogInteractionListener {

    companion object {
        /** Logging identifier. */
        private val TAG = "BowlerTeamListActivity"
    }

    /** Active tab. */
    private val currentTab: Int
        get() = pager_bowlers_teams.currentItem

    /** Handle visibility changes in the fab. */
    val fabVisibilityChangedListener = object : OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            super.onHidden(fab)

            when (currentTab) {
                0 -> fab?.setImageResource(R.drawable.ic_person_add_black_24dp)
                1 -> fab?.setImageResource(R.drawable.ic_group_add_black_24dp)
            }

            fab?.show()
        }
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bowler_team_list)

        configureToolbar()
        configureTabLayout()
        configureFab()
    }

    /**
     * Configure toolbar for rendering.
     */
    private fun configureToolbar() {
        setSupportActionBar(toolbar_bowlers_teams)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Configure tab layout for rendering.
     */
    private fun configureTabLayout() {
        tabs_bowlers_teams.addTab(tabs_bowlers_teams.newTab().setText(R.string.bowlers))
        tabs_bowlers_teams.addTab(tabs_bowlers_teams.newTab().setText(R.string.teams))
        pager_bowlers_teams.scrollingEnabled = false

        val adapter = BowlersTeamsPagerAdapter(supportFragmentManager, tabs_bowlers_teams.tabCount)
        pager_bowlers_teams.adapter = adapter

        pager_bowlers_teams.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs_bowlers_teams))
        tabs_bowlers_teams.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager_bowlers_teams.currentItem = tab.position

                if (fab.visibility == View.VISIBLE) {
                    fab.hide(fabVisibilityChangedListener)
                } else {
                    fabVisibilityChangedListener.onHidden(fab)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * Configure floating action buttons for rendering.
     */
    private fun configureFab() {
        fab.setImageResource(R.drawable.ic_person_add_black_24dp)

        fab.setOnClickListener {
            when (currentTab) {
                0 -> promptNewBowler()
                1 -> TODO("not implemented")
            }
        }
    }

    /** @Override */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    /** @Override */
    override fun onBackPressed() {
        super.onBackPressed()
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_transfer -> {
                // initiateTransfer()
                true
            }
            R.id.action_settings -> {
                openSettings()
                true
            }
            R.id.action_feedback -> {
                Email.sendEmail(
                        this,
                        resources.getString(R.string.feedback_email_recipient),
                        String.format(resources.getString(R.string.feedback_email_subject), BuildConfig.VERSION_CODE),
                        null
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Opens the settings activity.
     */
    private fun openSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    /**
     * Display a prompt to add a new bowler.
     */
    private fun promptNewBowler() {
        val newFragment = BowlerDialog.newInstance(null)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
    }

    /**
     * Callback to create a new [Bowler].
     *
     * @param name name of the new [Bowler]
     */
    override fun onCreateBowler(name: String) {
        launch(Android) {
            Bowler.createNewAndSave(this@BowlerTeamListActivity, name).await()
            val adapter = pager_bowlers_teams.adapter as? BowlersTeamsPagerAdapter
            val bowlerFragment = adapter?.getFragment(pager_bowlers_teams.currentItem) as? BowlerFragment
            bowlerFragment?.refreshBowlerList()
        }
    }

    /** @Override. */
    override fun onTeamSelected(team: Team) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** @Override. */
    override fun onBowlerSelected(bowler: Bowler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Manages pages for each tab.
     */
    internal class BowlersTeamsPagerAdapter(fm: FragmentManager, private var tabCount: Int): FragmentPagerAdapter(fm) {

        /** Weak references to the fragments in the pager. */
        private val mFragmentReferenceMap: MutableMap<Int, WeakReference<Fragment>> = HashMap()

        /** @Override. */
        override fun getItem(position: Int): Fragment? {
            val fragment: Fragment
            when (position) {
                0 -> fragment = BowlerFragment.newInstance()
                1 -> fragment = TeamFragment.newInstance()
                else -> return null
            }

            mFragmentReferenceMap.put(position, WeakReference(fragment))
            return fragment
        }

        /** @Override. */
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            mFragmentReferenceMap.remove(position)
        }

        /** @Override. */
        override fun getCount(): Int {
            return tabCount
        }

        /**
         * Get a reference to a fragment in the pager.
         *
         * @param position the fragment to get
         * @return the fragment at [position]
         */
        fun getFragment(position: Int): Fragment? {
            return mFragmentReferenceMap.get(position)?.get()
        }
    }
}
