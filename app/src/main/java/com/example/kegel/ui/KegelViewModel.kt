package com.example.kegel.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kegel.dataStore
import com.example.kegel.scheduler.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class KegelViewModel(private val context: Context) : ViewModel() {
    var uiState by mutableStateOf(KegelUiState())
        private set

    private val prefs = context.dataStore

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadPreferences()
            updateUiState()
        }
    }

    private suspend fun loadPreferences() {
        val prefsSnapshot = prefs.data.first()
        uiState = uiState.copy(
            notificationsEnabled = prefsSnapshot[NOTIFICATIONS_ENABLED_KEY] ?: true,
            kegelCount = prefsSnapshot[KEGEL_COUNT_KEY] ?: 3,
            meditationCount = prefsSnapshot[MEDITATION_COUNT_KEY] ?: 1,
            kegelDuration = prefsSnapshot[KEGEL_DURATION_KEY] ?: 2,
            meditationDuration = prefsSnapshot[MEDITATION_DURATION_KEY] ?: 5,
            activeStart = LocalTime.parse(prefsSnapshot[ACTIVE_START_KEY] ?: "08:00"),
            activeEnd = LocalTime.parse(prefsSnapshot[ACTIVE_END_KEY] ?: "20:00")
        )
    }

    private fun updateUiState() {
        uiState = uiState.copy(
            activePeriodLabel = "${uiState.activeStart} - ${uiState.activeEnd}",
            nextNotificationLabel = "Nenhuma agendada",
            dailySummary = "Faça ${uiState.kegelCount} Kegel(s) e ${uiState.meditationCount} meditação(ões) hoje.",
            historySummary = "Nenhuma atividade registrada hoje.",
            activeSessionLabel = "Nenhum horário em andamento",
            tipText = "Use respiração lenta durante cada exercício e mantenha boa postura."
        )
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        uiState = uiState.copy(notificationsEnabled = enabled)
        savePreference(NOTIFICATIONS_ENABLED_KEY, enabled)
        viewModelScope.launch { scheduleTodayNotifications(context) }
    }

    fun incrementKegelCount() {
        if (uiState.kegelCount < 10) {
            uiState = uiState.copy(kegelCount = uiState.kegelCount + 1)
            savePreference(KEGEL_COUNT_KEY, uiState.kegelCount)
        }
    }

    fun decrementKegelCount() {
        if (uiState.kegelCount > 0) {
            uiState = uiState.copy(kegelCount = uiState.kegelCount - 1)
            savePreference(KEGEL_COUNT_KEY, uiState.kegelCount)
        }
    }

    fun incrementMeditationCount() {
        if (uiState.meditationCount < 3) {
            uiState = uiState.copy(meditationCount = uiState.meditationCount + 1)
            savePreference(MEDITATION_COUNT_KEY, uiState.meditationCount)
        }
    }

    fun decrementMeditationCount() {
        if (uiState.meditationCount > 0) {
            uiState = uiState.copy(meditationCount = uiState.meditationCount - 1)
            savePreference(MEDITATION_COUNT_KEY, uiState.meditationCount)
        }
    }

    fun setKegelDuration(value: Int) {
        uiState = uiState.copy(kegelDuration = value)
        savePreference(KEGEL_DURATION_KEY, value)
    }

    fun setMeditationDuration(value: Int) {
        uiState = uiState.copy(meditationDuration = value)
        savePreference(MEDITATION_DURATION_KEY, value)
    }

    fun changeActivePeriodStart() {
        uiState = uiState.copy(activeStart = uiState.activeStart.minusHours(1))
        savePreference(ACTIVE_START_KEY, uiState.activeStart.toString())
        updateUiState()
    }

    fun changeActivePeriodEnd() {
        uiState = uiState.copy(activeEnd = uiState.activeEnd.plusHours(1))
        savePreference(ACTIVE_END_KEY, uiState.activeEnd.toString())
        updateUiState()
    }

    fun markActivityDone() {
        uiState = uiState.copy(historySummary = "Atividade concluída hoje.")
    }

    fun clearTodayHistory() {
        uiState = uiState.copy(historySummary = "Nenhuma atividade registrada hoje.")
    }

    fun toggleShowTips() {
        uiState = uiState.copy(tipText = "Respire devagar, segure a contração por 5 segundos e relaxe por 5 segundos.")
    }

    suspend fun scheduleTodayNotifications(context: Context) {
        NotificationScheduler.scheduleTodayNotifications(context, uiState)
    }

    private fun savePreference(key: androidx.datastore.preferences.core.Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    private fun savePreference(key: androidx.datastore.preferences.core.Preferences.Key<Int>, value: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    private fun savePreference(key: androidx.datastore.preferences.core.Preferences.Key<String>, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    companion object {
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val KEGEL_COUNT_KEY = intPreferencesKey("kegel_count")
        private val MEDITATION_COUNT_KEY = intPreferencesKey("meditation_count")
        private val KEGEL_DURATION_KEY = intPreferencesKey("kegel_duration")
        private val MEDITATION_DURATION_KEY = intPreferencesKey("meditation_duration")
        private val ACTIVE_START_KEY = stringPreferencesKey("active_start")
        private val ACTIVE_END_KEY = stringPreferencesKey("active_end")
        private const val NOTIFICATION_WORK_NAME = "kegel_notification_work"
    }
}
