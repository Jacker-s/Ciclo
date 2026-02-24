package com.ciclo21.app.data.local

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ciclo21_prefs", Context.MODE_PRIVATE)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean("onboarding_completed", completed).apply()
    }

    fun isOnboardingCompleted(): Boolean = sharedPreferences.getBoolean("onboarding_completed", false)

    fun saveCycleData(cycle: Int, period: Int, lastStartDate: LocalDate) {
        sharedPreferences.edit().apply {
            putInt("cycle_length", cycle)
            putInt("period_length", period)
            putString("last_period_start_date", lastStartDate.format(dateFormatter))
            apply()
        }
    }

    fun getCycleLength(): Int = sharedPreferences.getInt("cycle_length", 28)
    fun getPeriodLength(): Int = sharedPreferences.getInt("period_length", 5)
    
    fun getLastPeriodStartDate(): LocalDate {
        val dateString = sharedPreferences.getString("last_period_start_date", null)
        return dateString?.let { LocalDate.parse(it, dateFormatter) } ?: LocalDate.now().minusDays(28)
    }

    fun savePillSettings(brand: String, hour: Int, minute: Int, totalDoses: Int) {
        sharedPreferences.edit().apply {
            putString("pill_brand", brand)
            putInt("pill_hour", hour)
            putInt("pill_minute", minute)
            putInt("pill_total_doses", totalDoses)
            apply()
        }
    }

    fun getPillBrand(): String? = sharedPreferences.getString("pill_brand", null)
    fun getPillTotalDoses(): Int = sharedPreferences.getInt("pill_total_doses", 21)
    fun getPillTime(): LocalTime = LocalTime.of(
        sharedPreferences.getInt("pill_hour", 6),
        sharedPreferences.getInt("pill_minute", 0)
    )

    fun setPregnancyMode(active: Boolean) {
        sharedPreferences.edit().putBoolean("is_pregnancy_mode", active).apply()
    }

    fun isPregnancyMode(): Boolean = sharedPreferences.getBoolean("is_pregnancy_mode", false)

    fun savePregnancyStartDate(date: LocalDate) {
        sharedPreferences.edit().putString("pregnancy_start_date", date.format(dateFormatter)).apply()
    }

    fun getPregnancyStartDate(): LocalDate {
        val dateString = sharedPreferences.getString("pregnancy_start_date", null)
        return dateString?.let { LocalDate.parse(it, dateFormatter) } ?: LocalDate.now()
    }

    fun setWaterReminder(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("water_reminder", enabled).apply()
    }

    fun isWaterReminderEnabled(): Boolean = sharedPreferences.getBoolean("water_reminder", false)

    fun setBiometricLock(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("biometric_lock", enabled).apply()
    }

    fun isBiometricLockEnabled(): Boolean = sharedPreferences.getBoolean("biometric_lock", false)

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
