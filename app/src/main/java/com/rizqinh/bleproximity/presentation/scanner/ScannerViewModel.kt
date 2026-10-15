package com.rizqinh.bleproximity.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.domain.repository.BleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Scanner Dashboard Screen.
 *
 * Handles:
 * - Real-time BLE device stream collection.
 * - Dynamic sorting by signal strength (highest RSSI first).
 * - Filtering by search query (Name / MAC) and RSSI threshold slider.
 * - Auto-persistence to Room history upon target interaction.
 * - Observes Bluetooth adapter state changes.
 */
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: BleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private val _rawDevicesList = MutableStateFlow<List<BleDevice>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _minRssiThreshold = MutableStateFlow(-100)

    private var scanJob: Job? = null

    init {
        observeBluetoothState()
        combineFilteringAndSorting()
    }

    /**
     * Observes real-time Bluetooth adapter state (ON / OFF).
     */
    private fun observeBluetoothState() {
        repository.isBluetoothEnabled()
            .onEach { isEnabled ->
                _uiState.update { it.copy(isBluetoothEnabled = isEnabled) }
                if (!isEnabled) {
                    stopScanning()
                }
            }
            .launchIn(viewModelScope)

        repository.isScanning()
            .onEach { isScanning ->
                _uiState.update { it.copy(isScanning = isScanning) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Combines raw scanned devices stream with search query and RSSI slider threshold,
     * sorting by strongest signal (highest RSSI first) automatically.
     */
    private fun combineFilteringAndSorting() {
        combine(
            _rawDevicesList,
            _searchQuery,
            _minRssiThreshold
        ) { rawList, query, threshold ->
            rawList
                // Filter by minimum RSSI threshold slider
                .filter { device -> device.rssi >= threshold }
                // Filter by search query (Name or MAC address)
                .filter { device ->
                    if (query.isBlank()) {
                        true
                    } else {
                        device.name.contains(query, ignoreCase = true) ||
                                device.macAddress.contains(query, ignoreCase = true)
                    }
                }
                // Auto-sort by strongest signal (highest RSSI first)
                .sortedByDescending { device -> device.rssi }
        }.onEach { filteredAndSortedList ->
            _uiState.update { currentState ->
                currentState.copy(devices = filteredAndSortedList)
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Starts continuous BLE scanning stream.
     */
    fun startScanning() {
        if (!_uiState.value.isBluetoothEnabled || !_uiState.value.hasPermissions) return

        scanJob?.cancel()
        scanJob = repository.scanBleDevices()
            .onEach { rawDevices ->
                _rawDevicesList.value = rawDevices
                _uiState.update { it.copy(rawDevices = rawDevices) }

                // Automatically upsert detected devices into Room database history log
                rawDevices.forEach { device ->
                    viewModelScope.launch {
                        repository.saveDeviceHistory(device)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Stops continuous BLE scanning.
     */
    fun stopScanning() {
        scanJob?.cancel()
        scanJob = null
        repository.stopScan()
        _uiState.update { it.copy(isScanning = false) }
    }

    /**
     * Toggles scan state between active scanning and stopped.
     */
    fun toggleScanning() {
        if (_uiState.value.isScanning) {
            stopScanning()
        } else {
            startScanning()
        }
    }

    /**
     * Updates search query input.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Updates minimum RSSI slider threshold.
     */
    fun onRssiThresholdChanged(threshold: Int) {
        _minRssiThreshold.value = threshold
        _uiState.update { it.copy(minRssiThreshold = threshold) }
    }

    /**
     * Updates permission status from activity/composable level.
     */
    fun updatePermissionStatus(hasPermissions: Boolean) {
        _uiState.update { it.copy(hasPermissions = hasPermissions) }
        if (hasPermissions && _uiState.value.isBluetoothEnabled && !_uiState.value.isScanning) {
            startScanning()
        }
    }

    /**
     * Explicitly saves selected device to history log in Room DB.
     */
    fun saveDeviceToHistory(device: BleDevice) {
        viewModelScope.launch {
            repository.saveDeviceHistory(device)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
    }
}
