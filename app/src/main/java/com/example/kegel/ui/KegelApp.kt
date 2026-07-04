package com.example.kegel.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun KegelApp(viewModel: KegelViewModel) {
    val uiState = viewModel.uiState
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kegel Diário") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeSection(uiState, viewModel, scope)
                Divider()
                SettingsSection(uiState, viewModel, scope)
            }
        }
    )
}

@Composable
fun HomeSection(uiState: KegelUiState, viewModel: KegelViewModel, scope: androidx.compose.runtime.CoroutineScope) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Bem-vindo", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
        Text(uiState.dailySummary)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Próxima notificação:")
            Text(uiState.nextNotificationLabel)
        }

        Text("Atividade em andamento: ${uiState.activeSessionLabel}")
        Text("Estatísticas: ${uiState.historySummary}")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.markActivityDone() }) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Marcar concluído")
            }
            OutlinedButton(onClick = { viewModel.clearTodayHistory() }) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Limpar histórico")
            }
        }

        Text("Dicas: ${uiState.tipText}")
        TextButton(onClick = { viewModel.toggleShowTips() }) {
            Icon(Icons.Default.Info, contentDescription = null)
            Spacer(modifier = Modifier.padding(4.dp))
            Text("Ver dicas")
        }
    }
}

@Composable
fun SettingsSection(uiState: KegelUiState, viewModel: KegelViewModel, scope: androidx.compose.runtime.CoroutineScope) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Preferências", style = MaterialTheme.typography.h6)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Notificações")
            Switch(checked = uiState.notificationsEnabled, onCheckedChange = { viewModel.updateNotificationsEnabled(it) })
        }

        Text("Horário ativo: ${uiState.activePeriodLabel}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.changeActivePeriodStart() }) { Text("Início") }
            Button(onClick = { viewModel.changeActivePeriodEnd() }) { Text("Fim") }
        }

        Text("Exercícios de Kegel: ${uiState.kegelCount}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.decrementKegelCount() }) { Text("-") }
            Button(onClick = { viewModel.incrementKegelCount() }) { Text("+") }
        }

        Text("Meditações: ${uiState.meditationCount}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.decrementMeditationCount() }) { Text("-") }
            Button(onClick = { viewModel.incrementMeditationCount() }) { Text("+") }
        }

        Text("Duração exercício: ${uiState.kegelDuration} min")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.setKegelDuration(2) }) { Text("2 min") }
            Button(onClick = { viewModel.setKegelDuration(3) }) { Text("3 min") }
        }

        Text("Duração meditação: ${uiState.meditationDuration} min")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.setMeditationDuration(5) }) { Text("5 min") }
            Button(onClick = { viewModel.setMeditationDuration(10) }) { Text("10 min") }
        }
    }
}
