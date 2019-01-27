package ca.josephroque.bowlingcompanion.utils

import androidx.test.platform.app.InstrumentationRegistry
import ca.josephroque.bowlingcompanion.TestDates.APR_26_1995
import ca.josephroque.bowlingcompanion.TestDates.JAN_1_2019
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

/**
 * Copyright (C) 2019 Joseph Roque
 *
 * Test utility functions in DateUtils
 */
class DateUtilsInstrumentationTest {

    companion object {
        @BeforeClass
        fun initDateUtils() {
            DateUtils.init(InstrumentationRegistry.getInstrumentation().targetContext)
        }
    }

    @Test
    fun convertingDateToPretty_IsCorrect() {
        assertEquals("January 1, 2019", JAN_1_2019.pretty)
        assertEquals("April 26, 1995", APR_26_1995.pretty)
        assertEquals("Yesterday", DateUtils.Calendars.yesterday.time.pretty)
        assertEquals("Today", DateUtils.Calendars.today.time.pretty)
        assertEquals("Tomorrow", DateUtils.Calendars.tomorrow.time.pretty)
    }
}
