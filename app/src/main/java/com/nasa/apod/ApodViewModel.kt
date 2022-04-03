package com.nasa.apod

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasa.apod.repo.APIService
import kotlinx.coroutines.launch

class ApodViewModel : ViewModel() {
    private val _apod =
        mutableStateOf(Apod())
    var errorMessage: String by mutableStateOf("")
    val apod: MutableState<Apod>
        get() = _apod

    fun getApod() {
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                _apod.value = Apod()
                _apod.value = apiService.getApod()
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }
}