package com.example.kegel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.lifecycleScope
import com.example.kegel.ui.KegelApp
import com.example.kegel.ui.KegelViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: KegelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colors.background) {
                    KegelApp(viewModel)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.scheduleTodayNotifications(applicationContext)
        }
    }
}
