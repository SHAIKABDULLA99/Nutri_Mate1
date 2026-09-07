package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.model.WaterAlarmSlot
import java.util.Calendar

class WaterAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("alarm_title") ?: "💧 Drink Water Reminder / నీరు త్రాగే సమయం!"
        val message = intent?.getStringExtra("alarm_message") ?: "Time for a fresh glass of water! / 1 గ్లాసు తాజా నీరు త్రాగండి."
        WaterAlarmHelper.triggerAlarmNotification(context, title, message)
    }
}

object WaterAlarmHelper {
    const val CHANNEL_ID = "water_hydration_alarms"
    const val CHANNEL_NAME = "Water Drinking Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily alarms and reminders to drink water on time"
                enableVibration(true)
                enableLights(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun triggerAlarmNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        // Haptic Vibration feedback
        playVibration(context)

        // Sound alert
        playSound(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(9901, notification)
    }

    fun playSound(context: Context) {
        try {
            val alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, alert)
            ringtone?.play()
        } catch (_: Exception) { }
    }

    fun playVibration(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300), -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 300, 200, 300), -1)
                }
            }
        } catch (_: Exception) { }
    }

    fun scheduleWaterSlot(context: Context, slot: WaterAlarmSlot) {
        if (!slot.isAlarmEnabled) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, WaterAlarmReceiver::class.java).apply {
            putExtra("alarm_title", "⏰ ${slot.timeLabel} - నీరు త్రాగే సమయం / Drink Water!")
            putExtra("alarm_message", "${slot.titleTe} (${slot.titleEn}) - ${slot.targetMl}ml. ${slot.benefitTe}")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            slot.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, slot.hour)
            set(Calendar.MINUTE, slot.minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // In case exact alarm permission is restricted on specific Android 13+ devices, fallback smoothly
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    fun cancelWaterSlot(context: Context, slotId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            slotId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
