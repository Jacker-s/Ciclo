package com.ciclo21.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciclo21.app.data.local.AppDatabase
import com.ciclo21.app.data.local.PreferenceManager
import com.ciclo21.app.domain.manager.CycleManager
import com.ciclo21.app.domain.model.CyclePhase
import com.ciclo21.app.domain.model.UserPreferences
import com.ciclo21.app.ui.theme.PopUpData
import com.ciclo21.app.ui.theme.iOSBlue
import com.ciclo21.app.ui.theme.iOSPink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val cycleManager = CycleManager()
    private val preferenceManager = PreferenceManager(application)
    private val db = AppDatabase.getDatabase(application)
    private val recordDao = db.dailyRecordDao()
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            val isPregnancy = preferenceManager.isPregnancyMode()
            
            if (isPregnancy) {
                loadPregnancyData()
            } else {
                loadCycleData()
            }
        }
    }

    private suspend fun loadPregnancyData() {
        val startDate = preferenceManager.getPregnancyStartDate()
        val weeks = ChronoUnit.WEEKS.between(startDate, LocalDate.now()).toInt()
        val days = ChronoUnit.DAYS.between(startDate, LocalDate.now()).toInt() % 7
        val dueDate = startDate.plusWeeks(40)
        val daysUntilBirth = ChronoUnit.DAYS.between(LocalDate.now(), dueDate).toInt()

        val pregnancyInsight = PopUpData(
            title = "Sua Gestação: Semana $weeks",
            description = "Seu bebê está se desenvolvendo maravilhosamente bem!",
            tips = listOf(
                "🍼 Tamanho: Seu bebê é do tamanho de um " + getBabySize(weeks),
                "💪 Desenvolvimento: " + getPregnancyTip(weeks),
                "📅 DDP: Data prevista do parto em ${dueDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"))}",
                "🧘 Bem-estar: Pratique exercícios leves e mantenha o ácido fólico.",
                "🩺 Lembrete: Sua próxima consulta de pré-natal deve ser agendada em breve."
            ),
            color = iOSPink
        )

        _uiState.value = HomeUiState(
            isPregnancyMode = true,
            pregnancyWeek = weeks,
            pregnancyDays = days,
            daysUntilBirth = daysUntilBirth,
            dailyPopUpData = pregnancyInsight,
            currentPhase = CyclePhase.FOLICULAR, // Dummy para cor, usaremos rosa
            babySizeInfo = getBabySize(weeks)
        )
    }

    private suspend fun loadCycleData() {
        val cycleLen = preferenceManager.getCycleLength()
        val periodLen = preferenceManager.getPeriodLength()
        val lastStart = preferenceManager.getLastPeriodStartDate()
        
        val todayRecord = recordDao.getRecordByDate(LocalDate.now()).first()
        val water = todayRecord?.waterIntake ?: 0
        val isPillTakenToday = todayRecord?.pillTaken ?: false
        
        val allRecords = recordDao.getAllRecords().first()
        val dosesInCycle = allRecords.count { it.date.isAfter(lastStart.minusDays(1)) && it.pillTaken }
        
        val currentDay = cycleManager.calculateCurrentDay(lastStart, cycleLen)
        val phase = cycleManager.getPhaseForDay(currentDay, cycleLen, periodLen)
        val chance = cycleManager.getChanceOfPregnancy(currentDay, cycleLen)
        
        val statusPopUpData = PopUpData(
            title = "Status do seu Período",
            description = "Você está no Dia $currentDay do ciclo.",
            tips = listOf(
                "🌸 Fase ${phase.displayName}: ${phase.description}",
                "💊 Doses: $dosesInCycle tomadas neste ciclo.",
                "💡 Dica: Mantenha seu registro diário para predições precisas."
            ),
            color = phase.color
        )
        
        _uiState.value = HomeUiState(
            isPregnancyMode = false,
            currentDay = currentDay,
            cycleLength = cycleLen,
            currentPhase = phase,
            daysUntilNextPeriod = cycleManager.getDaysUntilNextPeriod(lastStart, cycleLen),
            pregnancyChance = chance,
            dailyPopUpData = statusPopUpData,
            waterIntake = water,
            pillTaken = isPillTakenToday,
            dosesCount = dosesInCycle
        )
    }

    private fun getBabySize(week: Int) = when(week) {
        in 0..4 -> "Grão de Papoula"
        in 5..8 -> "Mirtilo"
        in 9..12 -> "Limão"
        in 13..16 -> "Abacate"
        in 17..20 -> "Banana"
        in 21..24 -> "Milho"
        in 25..28 -> "Berinjela"
        in 29..32 -> "Couve-flor"
        in 33..36 -> "Abacaxi"
        else -> "Melancia"
    }

    private fun getPregnancyTip(week: Int) = when(week) {
        in 0..12 -> "O sistema nervoso e os órgãos vitais estão se formando."
        in 13..26 -> "O bebê já começa a ouvir sua voz e a se movimentar."
        else -> "Preparação final! O pulmão e o ganho de peso são o foco."
    }
}

data class HomeUiState(
    val isPregnancyMode: Boolean = false,
    val currentDay: Int = 1,
    val cycleLength: Int = 28,
    val currentPhase: CyclePhase = CyclePhase.FOLICULAR,
    val daysUntilNextPeriod: Int = 21,
    val pregnancyChance: String = "Baixa",
    val dailyPopUpData: PopUpData? = null,
    val waterIntake: Int = 0,
    val pillTaken: Boolean = false,
    val dosesCount: Int = 0,
    // Pregnancy specific
    val pregnancyWeek: Int = 0,
    val pregnancyDays: Int = 0,
    val daysUntilBirth: Int = 0,
    val babySizeInfo: String = "",
    val userPreferences: UserPreferences = UserPreferences()
)
