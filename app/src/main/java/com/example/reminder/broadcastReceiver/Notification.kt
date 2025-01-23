package com.example.reminder.broadcastReceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.reminder.AddNewPeople
import com.example.reminder.R

class Notification : BroadcastReceiver() {

    companion object {
        var notificationID = 0
        var channelID = "Chanel_Id_A"
        const val titleExtra = "title_extra"
        const val messageExtra = "message_extra"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)
        notificationCalling(context, intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context) {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"

        // Specify the custom sound URI
        val soundUri = Uri.parse("android.resource://com.example.reminder/raw/sound_a")

        // Create audio attributes for the sound
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Create the channel
        val channel = NotificationChannel(channelID, name,  NotificationManager.IMPORTANCE_HIGH).apply {
            description = desc
            setSound(soundUri, audioAttributes) // Set the custom sound
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun notificationCalling(context: Context, intent: Intent) {

        val notificationIntent = Intent(context, AddNewPeople::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationID, notification)
    }
}
