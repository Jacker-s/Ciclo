package com.ciclo21.app.data.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ciclo21.app.data.local.AppDatabase
import com.ciclo21.app.data.local.DailyRecord
import com.ciclo21.app.data.local.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

class PillAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_DISMISS_PILL") {
            handleDismiss(context)
            return
        }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNextAlarm(context)
            return
        }

        showAlarmNotification(context)
    }

    private fun handleDismiss(context: Context) {
        // 1. Cancela a notificação atual
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1001)

        // 2. Marca no banco de dados que a pílula foi tomada hoje
        markPillAsTaken(context)

        // 3. Agenda o alarme para o PRÓXIMO DIA no mesmo horário
        scheduleNextAlarm(context)
    }

    private fun showAlarmNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pill_alarm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarme do Anticoncepcional", NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val dismissIntent = Intent(context, PillAlarmReceiver::class.java).apply {
            action = "ACTION_DISMISS_PILL"
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Hora da Pílula! 💊")
            .setContentText("Toque em desligar para confirmar que tomou seu anticoncepcional.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "DESLIGAR E MARCAR COMO TOMADO", dismissPendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    private fun scheduleNextAlarm(context: Context) {
        val preferenceManager = PreferenceManager(context)
        val alarmTime = preferenceManager.getPillTime()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Define o horário para AMANHÃ
        val nextAlarm = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarmTime.hour)
            set(Calendar.MINUTE, alarmTime.minute)
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1) // Pula para o próximo dia
        }

        val intent = Intent(context, PillAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarm.timeInMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarm.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarm.timeInMillis, pendingIntent)
        }
    }

    private fun markPillAsTaken(context: Context) {
        val db = AppDatabase.getDatabase(context)
        val dao = db.dailyRecordDao()
        val today = LocalDate.now()

        CoroutineScope(Dispatchers.IO).launch {
            dao.insertRecord(DailyRecord(date = today, pillTaken = true))
        }
    }
}
