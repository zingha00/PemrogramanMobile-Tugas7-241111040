package com.utama.aplikasiloginsederhana

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init { loadEvents() }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _events.value = repository.getAllEvents()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            repository.addEvent(event)
            loadEvents()
        }
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            repository.deleteEvent(id)
            loadEvents()
        }
    }

    fun registerEvent(id: Int) {
        viewModelScope.launch {
            repository.setRegistered(id, true)
            loadEvents()
        }
    }
}