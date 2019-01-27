package ca.josephroque.bowlingcompanion.utils

import ca.josephroque.bowlingcompanion.TestDates.APR_26_1995
import ca.josephroque.bowlingcompanion.TestDates.JAN_1_2019
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Copyright (C) 2019 Joseph Roque
 *
 * Test utility functions in DateUtils
 */
class DateUtilsTest {

    @Test
    fun convertingSeriesDateToDate_IsCorrect() {
        assertEquals(JAN_1_2019.time, "2019-01-01 00:00:00".fromSeriesColumn.time)
        assertEquals(APR_26_1995.time, "1995-04-26 04:55:00".fromSeriesColumn.time)
    }

    @Test
    fun convertingDateToSeriesDate_IsCorrect() {
        assertEquals("2019-01-01 00:00:00", JAN_1_2019.forSeriesColumn)
        assertEquals("1995-04-26 04:55:00", APR_26_1995.forSeriesColumn)
    }

    @Test
    fun convertingDateToShort_IsCorrect() {
        assertEquals("01/01", JAN_1_2019.short)
        assertEquals("04/26", APR_26_1995.short)
    }

    @Test
    fun setToMidnight_IsCorrect() {
        // January 1, 2019 at midnight in GMT
        val jan12019GMT = Date(1546300800000L)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        calendar.set(2019, 0, 1)
        calendar.setToMidnight()

        assertEquals(jan12019GMT, calendar.time)
    }
}
