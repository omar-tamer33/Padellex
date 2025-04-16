package com.example.padellex.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.Adapters.OnTimeSelectedListener
import com.example.padellex.Adapters.TimeAdapter
import com.example.padellex.R
import com.example.padellex.Repositories.TimeSlotsRepository
import com.example.padellex.Repositories.UserBookingRepository
import com.example.padellex.databinding.ActivityBookingBinding
import com.example.padellex.model.CourtItem
import com.example.padellex.model.TimeSlot
import com.example.padellex.model.UserBookingItem
import com.example.padellex.viewContainer.CustomWeekDayBinder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class BookingActivity : AppCompatActivity() {
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var timeSlotsRepository: TimeSlotsRepository
    @Inject lateinit var userBookingRepository: UserBookingRepository
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
        binding.timeSlotRv.adapter = adapter
        courtItem = intent.getParcelableExtra("court")
        initCalendar()
        timeSlotsRepository.generateTodaySlotsIfNeeded(courtItem!!.id,currentDate.toString())
        generateTimeSlotsForDate(weekDayBinder.selectedDate.toString()){
            val  numOfHours = adapter.getSelectedSlots().size
            binding.priceCalcTv.text = "${calculatePrice(numOfHours)} EGP"
        }

        binding.mapBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("court", courtItem)
            startActivity(intent)
        }

        binding.bookBtn.setOnClickListener {
            val selectedSlots = adapter.getSelectedSlots()
            if (selectedSlots.isEmpty()){
                Toast.makeText(this,"select time to book!",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            bookSlots(selectedSlots, courtItem!!, weekDayBinder.selectedDate.toString())
            Toast.makeText(this,"Booking successful!",Toast.LENGTH_LONG).show()
        }
    }

    fun initCalendar(){
        val firstDayOfWeek = currentDate.dayOfWeek
        binding.weekCalendarView.setup(currentDate, currentDate, firstDayOfWeek)
        binding.weekCalendarView.scrollToWeek(currentDate)
        weekDayBinder = CustomWeekDayBinder(selectedColor = resources.getColor(R.color.Blue,null) , unSelectedColor = resources.getColor(R.color.black,null)) { weekDay ->
            val currentSelection = weekDayBinder.selectedDate
            binding.priceCalcTv.text = "0.0 EGP"
            if (currentSelection != weekDay.date) {
                weekDayBinder.selectedDate = weekDay.date
                timeSlotsRepository.generateTodaySlotsIfNeeded(courtId = courtItem!!.id , weekDay.date.toString())
                generateTimeSlotsForDate((weekDayBinder.selectedDate).toString()){
                    val  numOfHours = adapter.getSelectedSlots().size
                    binding.priceCalcTv.text = "${calculatePrice(numOfHours)} EGP"
                }
                binding.weekCalendarView.notifyDateChanged(weekDay.date)
                if (currentSelection != null) {
                    binding.weekCalendarView.notifyDateChanged(currentSelection)
                }
            }
        }
        binding.weekCalendarView.dayBinder = weekDayBinder
    }

    fun generateTimeSlotsForDate(dateStr : String , timeSelected : (MutableList<TimeSlot>) -> Unit) {
        timeSlotsRepository.getTimeSlots(dateStr = dateStr , courtId = courtItem!!.id) { timeSlots ->
            adapter.updateAdapter(timeSlots)
            adapter.clearSelections()
            adapter.onTimeItemSelectedListener = object : OnTimeSelectedListener{
                override fun onTimeSelected(
                    item: TimeSlot,
                    position: Int,
                    selectedSlots: MutableList<TimeSlot>
                ) {
                    timeSelected(selectedSlots)
                }
            }
        }
    }

    fun calculatePrice(numOfHours : Int) : Double{
        val price = courtItem!!.courtPrice
        return price * numOfHours
    }

    private fun bookSlots(listOfSlots : List<TimeSlot> , courtItem: CourtItem , dateStr: String) {
        val courtId = courtItem.id
        val userId = auth.currentUser!!.uid
        val bookingId = UUID.randomUUID().toString()
        val bookingTimeList = mutableListOf<String>()
        for (timeSlot in listOfSlots){
            timeSlotsRepository.bookTimeSlot(courtId,dateStr,timeSlot,userId)
            bookingTimeList.add(timeSlot.timeKey)
        }
        userBookingRepository.addUserBooking(userId,UserBookingItem(
            courtName = courtItem.courtName,
            courtPrice = calculatePrice(bookingTimeList.size).toString(),
            courtLocation = courtItem.courtLocation,
            bookingDate = dateStr,
            bookingTime = bookingTimeList,
            bookingId = bookingId,
            courtId = courtId,
            userId = userId))
    }
}