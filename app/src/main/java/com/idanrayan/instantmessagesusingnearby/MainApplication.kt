package com.idanrayan.instantmessagesusingnearby

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.idanrayan.instantmessagesusingnearby.domain.utils.Constants.CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        lateinit var res: Resources
    }

    override fun onCreate() {
        super.onCreate()
        res = this.resources
        createNotificationsChannel(applicationContext)
    }

}

private fun createNotificationsChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Messaging using nearby connections",
                importance
            )
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
