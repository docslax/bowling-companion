package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import ca.josephroque.bowlingcompanion.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Utility methods for date manipulation and display.
 */
object DateUtils {

    fun init(context: Context) {
        Strings.yesterday = context.resources.getString(R.string.yesterday)
        Strings.today = context.resources.getString(R.string.today)
        Strings.tomorrow = context.resources.getString(R.string.tomorrow)
    }

    // MARK: String resources

    object Strings {
        lateinit var today: String
        lateinit var yesterday: String
        lateinit var tomorrow: String
    }

    // MARK: Dates

    object Calendars {
        var today: Calendar = Calendar.getInstance().apply {
            setToMidnight()
        }

        var yesterday: Calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
            setToMidnight()
        }

        var tomorrow: Calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            setToMidnight()
        }
    }

    // MARK: DateUtils

    /**
     * Convert a series date [String] to a [Date].
     *
     * @param seriesDate date to format
     * @return a date object
     */
    fun seriesDateToDate(seriesDate: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
        return formatter.parse(seriesDate)
    }

    /**
     * Convert a [Date] to a series date [String].
     *
     * @param date date to format
     * @return a string suitable for a series
     */
    fun dateToSeriesDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
        return formatter.format(date)
    }

    /**
     * Converts [Date] to a cleaner format.
     *
     * @param date date to format
     * @return prettier format of string with full month name
     */
    fun dateToPretty(date: Date): String {
        val yesterday = Calendars.yesterday.time
        val today = Calendars.today.time
        val tomorrow = Calendars.tomorrow.time
        val twoDaysAway = Calendars.tomorrow.apply { add(Calendar.DAY_OF_MONTH, 1) }.time

        if (yesterday <= date && today > date) {
            return Strings.yesterday
        } else if (today <= date && tomorrow > date) {
            return Strings.today
        } else if (tomorrow <= date && twoDaysAway > date) {
            return Strings.tomorrow
        }

        val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.CANADA)
        return formatter.format(date)
    }

    /**
     * Converts [Date] to a shorter format.
     *
     * @param date date to format
     * @return shorter format of string with month and day
     */
    fun dateToShort(date: Date): String {
        val formatter = SimpleDateFormat("MM/dd", Locale.CANADA)
        return formatter.format(date)
    }
}

// MARK: Convenience functions

fun Calendar.setToMidnight() {
    set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), 0, 0, 0)
    set(Calendar.MILLISECOND, 0)
}

val Date.pretty: String
    get() = DateUtils.dateToPretty(this)

val Date.short: String
    get() = DateUtils.dateToShort(this)

val Date.forSeriesColumn: String
    get() = DateUtils.dateToSeriesDate(this)

val String.fromSeriesColumn: Date
    get() = DateUtils.seriesDateToDate(this)
