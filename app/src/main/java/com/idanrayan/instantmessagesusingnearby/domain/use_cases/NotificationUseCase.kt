package com.idanrayan.instantmessagesusingnearby.domain.use_cases

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.data.Message
import com.idanrayan.instantmessagesusingnearby.data.MessageType
import com.idanrayan.instantmessagesusingnearby.domain.utils.Constants.CHANNEL_ID
import com.idanrayan.instantmessagesusingnearby.domain.utils.Constants.CONVERSATION_URI
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject


@Module
@InstallIn(SingletonComponent::class)
class NotificationUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Show a notification with information
     */
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    operator fun invoke(message: Message, name: String) {
        val intent =
            Intent(
                Intent.ACTION_VIEW,
                "$CONVERSATION_URI/id=${message.userID}/name=${name}".toUri(),
                context,
                MainActivity::class.java
            )
        // Execute this intent after click the notification
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )
        // The notification builder
        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID).setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(context.getString(R.string.message_from, name))
                .setContentText(
                    when {
                        message.type == MessageType.MESSAGE.type -> message.message
                        message.type.startsWith(MessageType.IMAGE.type) -> context.getString(R.string.img)
                        else -> context.getString(R.string.document)
                    }
                )
                .setContentIntent(pendingIntent)

        // show notification
        // If id is constant then the notification will be replaced if a new one is arrived
        notificationManager.notify(message.id.toInt(), builder.build())
    }
}