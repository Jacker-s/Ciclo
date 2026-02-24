package com.ciclo21.app.data.util

import android.content.Context
import com.ciclo21.app.data.local.AppDatabase
import com.ciclo21.app.data.local.DailyRecord
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Duration

class SleepTracker(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("sleep_prefs", Context.MODE_PRIVATE)
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.dailyRecordDao()

    fun recordScreenOff() {
        sharedPreferences.edit().putLong("last_screen_off", System.currentTimeMillis()).apply()
    }

    suspend fun recordScreenOn() {
        val lastOff = sharedPreferences.getLong("last_screen_off", 0)
        if (lastOff == 0L) return

        val now = System.currentTimeMillis()
        val durationMillis = now - lastOff
        val hours = durationMillis.toFloat() / (1000 * 60 * 60)

        // Se o tempo desligado for maior que 3 horas, consideramos como sono
        if (hours >= 3f) {
            val today = LocalDate.now()
            val existingRecord = dao.getRecordByDate(today).first() ?: DailyRecord(date = today)
            
            // Somamos ao sono já registrado hoje (caso o usuário tenha acordado e voltado a dormir)
            val totalSleep = (existingRecord.sleepHours ?: 0f) + hours
            dao.insertRecord(existingRecord.copy(sleepHours = totalSleep))
            
            // Limpamos o último desligamento para não contar novamente no próximo 'on'
            sharedPreferences.edit().remove("last_screen_off").apply()
        }
    }
}
