package com.ciclo21.app.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ciclo21.app.data.local.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class CalendarUiState(
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val lastPeriodStart: LocalDate = LocalDate.now().minusDays(28)
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val preferenceManager = PreferenceManager(application)
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        _uiState.value = CalendarUiState(
            cycleLength = preferenceManager.getCycleLength(),
            periodLength = preferenceManager.getPeriodLength(),
            lastPeriodStart = preferenceManager.getLastPeriodStartDate()
        )
    }
}
