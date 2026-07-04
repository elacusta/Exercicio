package com.example.kegel.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object KegelPreferences {
    val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
    val KEGEL_COUNT_KEY = intPreferencesKey("kegel_count")
    val MEDITATION_COUNT_KEY = intPreferencesKey("meditation_count")
    val KEGEL_DURATION_KEY = intPreferencesKey("kegel_duration")
    val MEDITATION_DURATION_KEY = intPreferencesKey("meditation_duration")
    val ACTIVE_START_KEY = stringPreferencesKey("active_start")
    val ACTIVE_END_KEY = stringPreferencesKey("active_end")
}
