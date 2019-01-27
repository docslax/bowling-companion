package ca.josephroque.bowlingcompanion

import java.util.Calendar
import java.util.Date

object TestDates {
    val JAN_1_2019: Date by lazy {
        val calendar = Calendar.getInstance()
        calendar.set(2019, 0, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return@lazy calendar.time
    }

    val APR_26_1995: Date by lazy {
        val calendar = Calendar.getInstance()
        calendar.set(1995, 3, 26, 4, 55, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return@lazy calendar.time
    }
}
