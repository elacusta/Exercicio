package com.example.kegel.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kegel.scheduler.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.Default).launch {
                NotificationScheduler.scheduleFromPreferences(context.applicationContext)
            }
        }
    }
}
