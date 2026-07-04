package com.example.kegel.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.first
import com.example.kegel.KegelUiState
import com.example.kegel.data.KegelPreferences
import com.example.kegel.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object NotificationScheduler {
    private const val PREFS_NAME = "kegel_scheduler_prefs"
    private const val KEY_SCHEDULE_COUNT = "scheduled_count"
    private const val ALARM_REQUEST_CODE_BASE = 1000
    private const val ALARM_ACTION = "com.example.kegel.ACTION_ALARM"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    const val EXTRA_NOTIFICATION_TEXT = "extra_notification_text"

    suspend fun scheduleTodayNotifications(context: Context, uiState: KegelUiState) {
        if (!uiState.notificationsEnabled) {
            cancelScheduledNotifications(context)
            return
        }

        val start = uiState.activeStart
        val end = uiState.activeEnd
        if (start >= end) {
            cancelScheduledNotifications(context)
            return
        }

        val notificationCount = uiState.kegelCount + uiState.meditationCount
        if (notificationCount <= 0) {
            cancelScheduledNotifications(context)
            return
        }

        val minutes = Duration.between(start, end).toMinutes().toInt().coerceAtLeast(1)
        val offsets = generateOffsets(notificationCount, minutes)

        cancelScheduledNotifications(context)
        scheduleAlarms(context, uiState, offsets)
        saveScheduledCount(context, offsets.size)
    }

    suspend fun scheduleFromPreferences(context: Context) {
        val preferences = withContext(Dispatchers.IO) { context.dataStore.data.first() }
        val uiState = KegelUiState(
            notificationsEnabled = preferences[KegelPreferences.NOTIFICATIONS_ENABLED_KEY] ?: true,
            kegelCount = preferences[KegelPreferences.KEGEL_COUNT_KEY] ?: 3,
            meditationCount = preferences[KegelPreferences.MEDITATION_COUNT_KEY] ?: 1,
            kegelDuration = preferences[KegelPreferences.KEGEL_DURATION_KEY] ?: 2,
            meditationDuration = preferences[KegelPreferences.MEDITATION_DURATION_KEY] ?: 5,
            activeStart = LocalTime.parse(preferences[KegelPreferences.ACTIVE_START_KEY] ?: "08:00"),
            activeEnd = LocalTime.parse(preferences[KegelPreferences.ACTIVE_END_KEY] ?: "20:00")
        )

        scheduleTodayNotifications(context, uiState)
    }

    private fun scheduleAlarms(context: Context, uiState: KegelUiState, offsets: List<Int>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = LocalDateTime.now()
        val startDateTime = LocalDateTime.of(now.toLocalDate(), uiState.activeStart)

        offsets.forEachIndexed { index, offset ->
            val triggerTime = startDateTime.plusMinutes(offset.toLong())
            if (triggerTime.isAfter(now)) {
                val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
                    action = ALARM_ACTION
                    putExtra(EXTRA_NOTIFICATION_ID, index)
                    putExtra(EXTRA_NOTIFICATION_TEXT, "Hora do seu exercício de Kegel ou meditação")
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    ALARM_REQUEST_CODE_BASE + index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    pendingIntent
                )
            }
        }
    }

    fun cancelScheduledNotifications(context: Context) {
        val savedCount = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_SCHEDULE_COUNT, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (index in 0 until savedCount) {
            val intent = Intent(context, NotificationAlarmReceiver::class.java).apply { action = ALARM_ACTION }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE_BASE + index,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_SCHEDULE_COUNT, 0).apply()
    }

    private fun saveScheduledCount(context: Context, count: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_SCHEDULE_COUNT, count).apply()
    }

    private fun generateOffsets(count: Int, totalMinutes: Int): List<Int> {
        return if (count >= totalMinutes) {
            (0 until totalMinutes).toList()
        } else {
            val values = mutableSetOf<Int>()
            while (values.size < count) {
                values.add((0 until totalMinutes).random())
            }
            values.toList().sorted()
        }
    }
}
