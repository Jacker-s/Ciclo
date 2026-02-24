package com.ciclo21.app.domain.manager

import com.ciclo21.app.domain.model.CyclePhase

data class CycleStatus(
    val dayInCycle: Int = 0,
    val phase: CyclePhase? = null,
    val isFertile: Boolean = false,
    val isPeriod: Boolean = false,
    val isOvulation: Boolean = false,
    val isPast: Boolean = false
)
