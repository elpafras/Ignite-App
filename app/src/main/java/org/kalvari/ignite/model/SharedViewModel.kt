package org.kalvari.ignite.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private val _renungan = MutableLiveData<RenunganModel?>()
    val renungan: LiveData<RenunganModel?> get() = _renungan


    fun setRenungan(data: RenunganModel?) {
        _renungan.value = data
    }

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }
}