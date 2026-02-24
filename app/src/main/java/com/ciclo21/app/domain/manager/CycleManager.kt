package com.ciclo21.app.domain.manager

import com.ciclo21.app.domain.model.CyclePhase
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CycleManager {

    fun calculateCurrentDay(lastPeriodStart: LocalDate, cycleLength: Int): Int {
        val today = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(lastPeriodStart, today).toInt()
        val currentDay = (daysBetween % cycleLength) + 1
        return if (currentDay <= 0) currentDay + cycleLength else currentDay
    }

    fun getPhaseForDay(day: Int, cycleLength: Int, periodLength: Int): CyclePhase {
        return when (day) {
            in 1..periodLength -> CyclePhase.MENSTRUACAO
            in (periodLength + 1)..(cycleLength / 2 - 2) -> CyclePhase.FOLICULAR
            in (cycleLength / 2 - 1)..(cycleLength / 2 + 1) -> CyclePhase.OVULACAO
            else -> CyclePhase.LUTEA
        }
    }

    fun getChanceOfPregnancy(day: Int, cycleLength: Int): String {
        val ovulationDay = cycleLength / 2
        return when (day) {
            in (ovulationDay - 3)..(ovulationDay + 1) -> "Alta"
            in (ovulationDay - 5)..(ovulationDay - 4) -> "Média"
            else -> "Baixa"
        }
    }

    fun getDaysUntilNextPeriod(lastPeriodStart: LocalDate, cycleLength: Int): Int {
        val currentCycleDay = calculateCurrentDay(lastPeriodStart, cycleLength)
        return cycleLength - currentCycleDay + 1
    }

    fun getStatusForDate(targetDate: LocalDate, lastPeriodStart: LocalDate, cycleLength: Int, periodLength: Int): CycleStatus {
        val daysBetween = ChronoUnit.DAYS.between(lastPeriodStart, targetDate).toInt()
        val dayInCycle = ((daysBetween % cycleLength) + cycleLength) % cycleLength + 1
        val phase = getPhaseForDay(dayInCycle, cycleLength, periodLength)
        
        return CycleStatus(
            dayInCycle = dayInCycle,
            phase = phase,
            isFertile = dayInCycle in (cycleLength / 2 - 3)..(cycleLength / 2 + 1),
            isPeriod = dayInCycle in 1..periodLength,
            isOvulation = dayInCycle == (cycleLength / 2)
        )
    }
}
