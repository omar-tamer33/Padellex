package com.example.padellexadmin.utilities

import com.example.padellexadmin.model.TimeSlot
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object TimeSlotUtils {
    fun generateTimeSlotsMapForDate(dateStr: String, startHour: Int = 6, endHour: Int = 23): Map<String, TimeSlot> {
        val slots = mutableMapOf<String, TimeSlot>()
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()

        for (hour in startHour..endHour) {
            val period = if (hour < 12) "AM" else "PM"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12

            val timeKey = String.format("%02d:00 %s", displayHour, period)
            val dateTimeString = "$dateStr ${String.format("%02d:00", displayHour)} $period"
            val slotDate = sdf.parse(dateTimeString)

            slots[timeKey] = TimeSlot(
                timeKey = timeKey,
                booked = false,
                userId = null,
                timestamp = slotDate?.time ?: 0L
            )
        }
        return slots
    }
}