package ca.josephroque.bowlingcompanion.statistics.impl.average

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Average score in the 12th game of a series.
 */
class Game12AverageStatistic(total: Int, divisor: Int) : PerGameAverageStatistic(total, divisor) {

    // MARK: Overrides

    override val gameNumber = 12
    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::Game12AverageStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_average_12
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(total = p.readInt(), divisor = p.readInt())
}
