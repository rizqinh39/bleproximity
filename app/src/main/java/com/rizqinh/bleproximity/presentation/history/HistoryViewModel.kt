package com.rizqinh.bleproximity.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.domain.repository.BleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing and displaying persistent device history log screen.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: BleRepository,
) : ViewModel() {

    /**
     * Hot StateFlow stream of history devices retrieved from Room Database sorted by lastSeenTimestamp DESC.
     */
    val historyDevices: StateFlow<List<BleDevice>> = repository.getDeviceHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    /**
     * Clears all stored device history records from local Room Database.
     */
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
