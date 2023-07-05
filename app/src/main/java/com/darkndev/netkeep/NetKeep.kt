package com.darkndev.netkeep

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.darkndev.netkeep.api.NetKeepWorkerFactory
import com.darkndev.netkeep.utils.Constants.NOTIFICATION_CHANNEL_1
import com.darkndev.netkeep.utils.sdkVersion26AndAbove
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NetKeep : Application(), Configuration.Provider {

    @Inject
    lateinit var factory: NetKeepWorkerFactory

    override fun onCreate() {
        super.onCreate()
        sdkVersion26AndAbove {
            val channel1 = NotificationChannel(
                NOTIFICATION_CHANNEL_1,
                "Sync",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel1)
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val delegate = DelegatingWorkerFactory()
        delegate.addFactory(factory)
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(delegate)
            .build()
    }

}