package com.ciclo21.app.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class UserPreferences(
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val lastPeriodStartDate: LocalDate? = null,
    val isOnboardingCompleted: Boolean = false,
    val isPregnancyMode: Boolean = false,
    val pregnancyStartDate: LocalDate? = null,
    val selectedContraceptive: String? = null,
    val pillReminderTime: LocalTime = LocalTime.of(6, 0)
)
