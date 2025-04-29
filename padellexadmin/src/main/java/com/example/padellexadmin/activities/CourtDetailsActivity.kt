package com.example.padellexadmin.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.padellex.viewContainer.CustomWeekDayBinder
import com.example.padellexadmin.Adapters.OnTimeClickListener
import com.example.padellexadmin.Adapters.TimeAdapter
import com.example.padellexadmin.R
import com.example.padellexadmin.databinding.ActivityCourtDetailsBinding
import com.example.padellexadmin.fragments.BottomSheetFragment
import com.example.padellexadmin.model.CourtData
import com.example.padellexadmin.model.TimeSlot
import com.example.padellexadmin.viewModels.CourtDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class CourtDetailsActivity : AppCompatActivity() {
    private val viewModel : CourtDetailsViewModel by viewModels()
    lateinit var binding: ActivityCourtDetailsBinding
    lateinit var weekDayBinder: CustomWeekDayBinder
    lateinit var courtData : CourtData
    lateinit var adapter: TimeAdapter
    val currentDate = LocalDate.now()
    var selectedDateStr = currentDate.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourtDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = TimeAdapter(mutableListOf())
        binding.timeSlotRv.adapter = adapter
        adapter.onTimeItemClickListener = object : OnTimeClickListener{
            override fun onTimeClick(timeSlot: TimeSlot, position: Int) {
                viewModel.timeSlotAvailability(courtId = courtData.id, dateStr = selectedDateStr, timeSlot = timeSlot)
            }

        }
        courtData = intent.getParcelableExtra("courtData")!!
        viewModel.getCourtDetails(courtData.id)
        observeViewModel()
        initCalendar()
        viewModel.generateTodaySlotsIfNeeded(courtData.id,currentDate.toString())
        viewModel.generateTimeSlotsForDate(currentDate.toString(),courtData.id)

        binding.editFab.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment(courtData, courtId = courtData.id){ courtName, courtLocation, courtPrice, courtLatitude, courtLongitude ->
                viewModel.updateCourt(CourtData(courtData.id,courtName,courtPrice.toDouble(),courtLocation,false,courtLatitude.toDouble(),courtLongitude.toDouble()))
                viewModel.getCourtDetails(courtData.id)
            }
            bottomSheetFragment.show(supportFragmentManager,"BottomSheetFragment")
        }

    }


    fun initCalendar(){
        val firstDayOfWeek = currentDate.dayOfWeek
        binding.weekCalendarView.setup(currentDate, currentDate, firstDayOfWeek)
        binding.weekCalendarView.scrollToWeek(currentDate)
            weekDayBinder = CustomWeekDayBinder(selectedColor = resources.getColor(R.color.purple,null) , unSelectedColor = resources.getColor(R.color.black,null)) { weekDay ->
                val currentSelection = weekDayBinder.selectedDate
                if (currentSelection != weekDay.date) {
                    weekDayBinder.selectedDate = weekDay.date
                    selectedDateStr = weekDay.date.toString()
                    viewModel.generateTodaySlotsIfNeeded(courtId = courtData.id , weekDay.date.toString())
                    binding.weekCalendarView.notifyDateChanged(weekDay.date)
                    viewModel.generateTimeSlotsForDate(weekDay.date.toString(),courtData.id)
                    if (currentSelection != null) {
                        binding.weekCalendarView.notifyDateChanged(currentSelection)
                    }
                }
            }
        binding.weekCalendarView.dayBinder = weekDayBinder
    }





    private fun observeViewModel(){
        viewModel.list.observe(this){timeSlots->
            adapter.updateAdapter(timeSlots)
        }


        viewModel.courtDataSuccess.observe(this){courtDetails->
            if (courtDetails != null) {
                courtData = courtDetails
                binding.courtNameTv.text = courtData.courtName
                binding.courtLocationTv.text = courtData.courtLocation
                binding.courtPriceTv.text = courtData.courtPrice.toString()
                Glide.with(this@CourtDetailsActivity)
                    .load(courtData.imageUrl)
                    .placeholder(R.drawable.court)
                    .into(binding.courtImage)
            }
        }
    }
}