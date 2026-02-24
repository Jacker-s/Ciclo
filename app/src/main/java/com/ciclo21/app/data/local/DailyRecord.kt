package com.ciclo21.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey val date: LocalDate,
    val symptoms: List<String> = emptyList(),
    val mood: String? = null,
    val flowIntensity: String? = null,
    val sexualDrive: String? = null,
    val sexualActivity: String? = null,
    val dischargeType: String? = null,
    val waterIntake: Int = 0,
    val weight: Float? = null,
    val sleepHours: Float? = null,
    val physicalActivity: String? = null,
    val note: String = "",
    val isPeriodDay: Boolean = false,
    val pillTaken: Boolean = false,
    val babyKicks: Int = 0,
    val contractions: Int = 0,
    val bloodPressure: String? = null,
    val vitaminsTaken: Boolean = false
)
