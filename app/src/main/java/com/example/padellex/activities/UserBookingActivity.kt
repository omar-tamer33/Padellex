package com.example.padellex.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.padellex.Adapters.OnDeleteClickListener
import com.example.padellex.Adapters.UserBookingAdapter
import com.example.padellex.R
import com.example.padellex.databinding.ActivityUserBookingBinding
import com.example.padellex.model.UserBookingItem
import com.example.padellex.viewContainer.CustomWeekDayBinder
import com.example.padellex.viewModels.UserBookingViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject


@AndroidEntryPoint
class UserBookingActivity : AppCompatActivity() {
    val viewModel : UserBookingViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityUserBookingBinding
    val currentDate = LocalDate.now()
    lateinit var userId : String
    lateinit var weekDayBinder: CustomWeekDayBinder
    val userBookingList = mutableListOf<UserBookingItem>()
    lateinit var adapter: UserBookingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = auth.currentUser!!.uid
        observeViewModel()
        adapter = UserBookingAdapter(userBookingList)
        binding.userBookingRv.adapter = adapter
        adapter.deleteBtnClickListener = object : OnDeleteClickListener {
            override fun onDeleteClick(userBookingItem: UserBookingItem, position: Int) {
                viewModel.onDeleteClick(userBookingItem)
            }
        }
        initCalendar()
        viewModel.getUserBookingByDate(userId,weekDayBinder.selectedDate.toString())
    }

    private fun initCalendar(){
        val firstDayOfWeek = currentDate.dayOfWeek
        binding.weekCalendarView.setup(currentDate, currentDate, firstDayOfWeek)
        binding.weekCalendarView.scrollToWeek(currentDate)
        weekDayBinder = CustomWeekDayBinder(selectedColor = resources.getColor(R.color.purple,null) , unSelectedColor = resources.getColor(
            R.color.black,null)) { weekDay ->
            val currentSelection = weekDayBinder.selectedDate
            if (currentSelection != weekDay.date) {
                weekDayBinder.selectedDate = weekDay.date
                viewModel.getUserBookingByDate(userId,weekDayBinder.selectedDate.toString())
                binding.weekCalendarView.notifyDateChanged(weekDay.date)
                if (currentSelection != null) {
                    binding.weekCalendarView.notifyDateChanged(currentSelection)
                }
            }
        }
        binding.weekCalendarView.dayBinder = weekDayBinder
    }

    private fun showEmptyAnimation(){
        binding.userBookingRv.isVisible = false
        binding.emptyTv.isVisible = true
        binding.emptyAnimation.isVisible = true
    }

    private fun hideEmptyAnimation(){
        binding.userBookingRv.isVisible = true
        binding.emptyTv.isVisible = false
        binding.emptyAnimation.isVisible = false
    }

    private fun observeViewModel(){
        viewModel.bookingList.observe(this){list->
            if (list.isNotEmpty()) {
                userBookingList.clear()
                userBookingList.addAll(list)
                adapter.notifyDataSetChanged()
                hideEmptyAnimation()
            } else {
                userBookingList.clear()
                adapter.notifyDataSetChanged()
                showEmptyAnimation()
            }
        }

        viewModel.deleteBookingEvent.observe(this){userBookingItem ->
            val index = userBookingList.indexOf(userBookingItem)
            if (index != -1){
                userBookingList.removeAt(index)
                adapter.notifyItemRemoved(index)
                viewModel.preformDelete(userBookingItem,auth.currentUser!!.uid)
            }
        }

        viewModel.userDelete.observe(this){isUserDeleted->
            if (isUserDeleted){
                auth.signOut()
                val intent = Intent(
                    this@UserBookingActivity, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }
    }
}