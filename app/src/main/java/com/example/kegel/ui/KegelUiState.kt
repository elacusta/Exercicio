package com.example.kegel.ui

import java.time.LocalTime

data class KegelUiState(
    val notificationsEnabled: Boolean = true,
    val kegelCount: Int = 3,
    val meditationCount: Int = 1,
    val kegelDuration: Int = 2,
    val meditationDuration: Int = 5,
    val activeStart: LocalTime = LocalTime.of(8, 0),
    val activeEnd: LocalTime = LocalTime.of(20, 0),
    val activePeriodLabel: String = "08:00 - 20:00",
    val nextNotificationLabel: String = "Nenhuma agendada",
    val dailySummary: String = "",
    val historySummary: String = "",
    val activeSessionLabel: String = "",
    val tipText: String = ""
)
