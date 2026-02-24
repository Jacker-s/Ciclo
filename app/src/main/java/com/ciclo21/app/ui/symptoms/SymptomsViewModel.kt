package com.ciclo21.app.ui.symptoms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciclo21.app.data.local.AppDatabase
import com.ciclo21.app.data.local.DailyRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class SymptomsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val recordDao = db.dailyRecordDao()

    private val _uiState = MutableStateFlow(SymptomsUiState())
    val uiState: StateFlow<SymptomsUiState> = _uiState.asStateFlow()

    init {
        loadTodayRecord()
    }

    private fun loadTodayRecord() {
        viewModelScope.launch {
            val record = recordDao.getRecordByDate(LocalDate.now()).first() ?: DailyRecord(date = LocalDate.now())
            _uiState.value = SymptomsUiState(
                waterIntake = record.waterIntake,
                mood = record.mood,
                flowIntensity = record.flowIntensity,
                sexualDrive = record.sexualDrive,
                sexualActivity = record.sexualActivity == "Sim",
                symptoms = record.symptoms,
                note = record.note,
                weight = record.weight ?: 65.0f,
                sleepHours = record.sleepHours ?: 7.5f
            )
        }
    }

    fun updateMood(mood: String) { _uiState.value = _uiState.value.copy(mood = mood) }
    fun updateFlow(flow: String) { _uiState.value = _uiState.value.copy(flowIntensity = flow) }
    fun updateLibido(libido: String) { _uiState.value = _uiState.value.copy(sexualDrive = libido) }
    fun toggleSymptom(symptom: String) {
        val current = _uiState.value.symptoms.toMutableList()
        if (current.contains(symptom)) current.remove(symptom) else current.add(symptom)
        _uiState.value = _uiState.value.copy(symptoms = current)
    }
    fun updateWater(count: Int) { _uiState.value = _uiState.value.copy(waterIntake = count) }
    fun updateSex(active: Boolean) { _uiState.value = _uiState.value.copy(sexualActivity = active) }
    fun updateNote(note: String) { _uiState.value = _uiState.value.copy(note = note) }

    fun saveRecord(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val record = DailyRecord(
                date = LocalDate.now(),
                symptoms = state.symptoms,
                mood = state.mood,
                flowIntensity = state.flowIntensity,
                sexualDrive = state.sexualDrive,
                sexualActivity = if (state.sexualActivity) "Sim" else "Não",
                waterIntake = state.waterIntake,
                note = state.note,
                weight = state.weight,
                sleepHours = state.sleepHours
            )
            recordDao.insertRecord(record)
            onSuccess()
        }
    }
}

data class SymptomsUiState(
    val waterIntake: Int = 0,
    val mood: String? = null,
    val flowIntensity: String? = null,
    val sexualDrive: String? = null,
    val sexualActivity: Boolean = false,
    val symptoms: List<String> = emptyList(),
    val note: String = "",
    val weight: Float = 65.0f,
    val sleepHours: Float = 7.5f
)
