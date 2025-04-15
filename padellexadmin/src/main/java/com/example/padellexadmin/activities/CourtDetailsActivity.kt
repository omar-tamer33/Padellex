package com.example.padellexadmin.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.viewContainer.CustomWeekDayBinder
import com.example.padellexadmin.Adapters.CourtAdapter
import com.example.padellexadmin.Adapters.OnTimeClickListener
import com.example.padellexadmin.Adapters.TimeAdapter
import com.example.padellexadmin.R
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.Repositories.TimeSlotsRepository
import com.example.padellexadmin.databinding.ActivityCourtDetailsBinding
import com.example.padellexadmin.model.CourtData
import com.example.padellexadmin.model.TimeSlot
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate

class CourtDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityCourtDetailsBinding
    lateinit var weekDayBinder: CustomWeekDayBinder
    lateinit var courtData : CourtData
    lateinit var adapter: TimeAdapter
    val currentDate = LocalDate.now()
    val db = FirebaseDatabase.getInstance()
    val timeSlotsRepository = TimeSlotsRepository(db)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourtDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = TimeAdapter(mutableListOf())
        binding.timeSlotRv.adapter = adapter
        courtData = intent.getParcelableExtra("courtData")!!
        initCalendar()
        timeSlotsRepository.generateTodaySlotsIfNeeded(courtData.id,currentDate.toString())
        generateTimeSlotsForDate(currentDate.toString()){ timeSlot ->
            timeSlotsRepository.timeSlotAvailability(courtId = courtData.id, dateStr = currentDate.toString(), timeSlot = timeSlot)
        }



    }

    fun initCalendar(){
        val firstDayOfWeek = currentDate.dayOfWeek
        binding.weekCalendarView.setup(currentDate, currentDate, firstDayOfWeek)
        binding.weekCalendarView.scrollToWeek(currentDate)
            weekDayBinder = CustomWeekDayBinder(selectedColor = resources.getColor(R.color.Blue,null) , unSelectedColor = resources.getColor(R.color.black,null)) { weekDay ->
                val currentSelection = weekDayBinder.selectedDate
                if (currentSelection != weekDay.date) {
                    weekDayBinder.selectedDate = weekDay.date
                    timeSlotsRepository.generateTodaySlotsIfNeeded(courtId = courtData.id , weekDay.date.toString())
                    binding.weekCalendarView.notifyDateChanged(weekDay.date)
                    generateTimeSlotsForDate(weekDay.date.toString()){ timeSlot ->
                        timeSlotsRepository.timeSlotAvailability(courtId = courtData.id, dateStr = weekDay.date.toString(), timeSlot = timeSlot)
                    }
                    if (currentSelection != null) {
                        binding.weekCalendarView.notifyDateChanged(currentSelection)
                    }
                }
            }
        binding.weekCalendarView.dayBinder = weekDayBinder
    }

    fun generateTimeSlotsForDate(dateStr : String , timeClick : (TimeSlot) -> Unit) {
     timeSlotsRepository.getTimeSlots(dateStr = dateStr , courtId = courtData.id) { timeSlots ->
            adapter.updateAdapter(timeSlots)
            adapter.onTimeItemClickListener = object : OnTimeClickListener{
                override fun onTimeClick(item: TimeSlot, position: Int) {
                    timeClick(item)
                }
            }
        }
    }
}