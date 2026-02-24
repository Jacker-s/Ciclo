package com.ciclo21.app.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ciclo21.app.data.receiver.PillAlarmReceiver
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedulePillAlarm(time: LocalTime) {
        val intent = Intent(context, PillAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val scheduleTime = LocalDate.now().atTime(time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val finalTime = if (scheduleTime <= System.currentTimeMillis()) {
            scheduleTime + 24 * 60 * 60 * 1000
        } else {
            scheduleTime
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            finalTime,
            pendingIntent
        )
    }

    fun cancelPillAlarm() {
        val intent = Intent(context, PillAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
