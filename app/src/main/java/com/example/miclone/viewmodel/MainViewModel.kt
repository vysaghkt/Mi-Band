package com.example.miclone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.miclone.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    application: Application
): AndroidViewModel(application) {

    val readStepGoal: LiveData<Int> = dataStoreRepository.getStepGoals.asLiveData()

    fun storeStepGoals(value : Int) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.storeStepGoal(value)
    }
}