package com.example.padellex.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.padellex.Adapters.TimeAdapter
import com.example.padellex.R
import com.example.padellex.databinding.ActivityBookingBinding
import com.example.padellex.model.CourtItem
import com.example.padellex.viewContainer.CustomWeekDayBinder
import com.example.padellex.viewModels.BookingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate


@AndroidEntryPoint
class BookingActivity : AppCompatActivity() {
    val viewModel : BookingViewModel by viewModels()
    lateinit var binding: ActivityBookingBinding
    var courtItem: CourtItem? = null
    val currentDate = LocalDate.now()
    lateinit var weekDayBinder: CustomWeekDayBinder
    lateinit var adapter: TimeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = TimeAdapter(mutableListOf())
        adapter.onSelectionChanged = {
            val numOfHours = adapter.getSelectedSlots().size
            binding.priceCalcTv.text = "${viewModel.calculatePrice(numOfHours, courtItem!!.courtPrice)} EGP"
        }
        binding.timeSlotRv.adapter = adapter
        courtItem = intent.getParcelableExtra("court")
        binding.courtNameTv.text = courtItem?.courtName
        binding.courtLocationTv.text = courtItem?.courtLocation
        binding.courtPriceTv.text = courtItem?.courtPrice.toString()
        Glide.with(this)
            .load(courtItem?.imageUrl)
            .placeholder(R.drawable.court)
            .into(binding.courtImage)
        initCalendar()
        observeViewModel()


        viewModel.generateTodaySlotsIfNeeded(courtItem!!.id,currentDate.toString())
        viewModel.generateTimeSlotsForDate(weekDayBinder.selectedDate.toString() , courtItem!!.id)

        binding.mapBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("court", courtItem)
            startActivity(intent)
        }

        binding.bookBtn.setOnClickListener {
            val selectedSlots = adapter.getSelectedSlots()
            if (selectedSlots.isEmpty()){
                Toast.makeText(this,"select time to book!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.bookSlots(selectedSlots, courtItem!!, weekDayBinder.selectedDate.toString())
            showAlertDialog()
        }
    }

    private fun showAlertDialog(){
        val dialogSuccess = LayoutInflater.from(this).inflate(R.layout.dialog_success,null)
        val alertDialog = AlertDialog.Builder(this@BookingActivity,).setView(dialogSuccess).create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
        Handler(Looper.getMainLooper()).postDelayed({alertDialog.dismiss()},3000)
    }
    private fun initCalendar(){
        val firstDayOfWeek = currentDate.dayOfWeek
        binding.weekCalendarView.setup(currentDate, currentDate, firstDayOfWeek)
        binding.weekCalendarView.scrollToWeek(currentDate)
        weekDayBinder = CustomWeekDayBinder(selectedColor = resources.getColor(R.color.purple,null) , unSelectedColor = resources.getColor(R.color.black,null)) { weekDay ->
            val currentSelection = weekDayBinder.selectedDate
            if (currentSelection != weekDay.date) {
                weekDayBinder.selectedDate = weekDay.date
                viewModel.generateTodaySlotsIfNeeded(courtId = courtItem!!.id , weekDay.date.toString())
                viewModel.generateTimeSlotsForDate((weekDayBinder.selectedDate).toString() , courtItem!!.id)
                binding.weekCalendarView.notifyDateChanged(weekDay.date)
                if (currentSelection != null) {
                    binding.weekCalendarView.notifyDateChanged(currentSelection)
                }
            }
        }
        binding.weekCalendarView.dayBinder = weekDayBinder
    }

    private fun observeViewModel() {
        viewModel.list.observe(this) { timeSlots ->
            adapter.updateAdapter(timeSlots)
            adapter.clearSelections()
            binding.priceCalcTv.setText(R.string._0_0_egp)
        }
    }
}