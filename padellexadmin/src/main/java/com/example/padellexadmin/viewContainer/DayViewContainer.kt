package com.example.padellex.viewContainer

import android.view.View
import com.example.padellexadmin.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


class CustomWeekDayBinder(val selectedColor : Int,val unSelectedColor : Int,val onDateSelected : (WeekDay) -> Unit) : WeekDayBinder<DayViewContainer> {
     var selectedDate: LocalDate? = LocalDate.now()
    override fun bind(container: DayViewContainer, data: WeekDay) {
        container.calendarWeekDayText.text = data.date.dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault())
        container.calendarDayText.text = "${data.date.dayOfMonth}"
        if (selectedDate == data.date){
            container.calendarWeekDayText.setTextColor(selectedColor)
            container.calendarDayText.setTextColor(selectedColor)
        }else{
            container.calendarWeekDayText.setTextColor(unSelectedColor)
            container.calendarDayText.setTextColor(unSelectedColor)
        }
        container.view.setOnClickListener {
            onDateSelected(data)
        }
    }

    override fun create(view: View): DayViewContainer {
        return DayViewContainer(view)
    }

}

class DayViewContainer(view: View) : ViewContainer(view) {

    val calendarDayText = CalendarDayLayoutBinding.bind(view).calendarDayText
    val calendarWeekDayText = CalendarDayLayoutBinding.bind(view).calendarWeekDayText
}