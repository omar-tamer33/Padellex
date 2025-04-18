package com.example.padellex.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padellex.Repositories.CourtsRepository
import com.example.padellex.model.CourtItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourtsViewModel @Inject constructor(private val courtsRepository: CourtsRepository) : ViewModel() {
    val list = MutableLiveData<List<CourtItem>>()


     fun getCourtData() {
        viewModelScope.launch {
           courtsRepository.getAllCourts(){courtsList->
               list.postValue(courtsList)
           }
        }

    }
}