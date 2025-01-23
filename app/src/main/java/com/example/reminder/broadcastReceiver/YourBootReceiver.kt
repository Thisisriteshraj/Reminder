package com.example.reminder.broadcastReceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.Room
import com.example.reminder.broadcastReceiver.Notification.Companion.messageExtra
import com.example.reminder.broadcastReceiver.Notification.Companion.titleExtra
import com.example.reminder.room.User
import com.example.reminder.room.UserDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class YourBootReceiver : BroadcastReceiver() {

    private lateinit var userDataBase: UserDataBase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Toast.makeText(context, "Device Boot Completed!", Toast.LENGTH_LONG).show()
            Log.d("BootCompleteReceiver", "Boot completed successfully!")

            userDataBase = Room.databaseBuilder(context, UserDataBase::class.java, "USER")
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                val userList = userDataBase.studentDAO().getAllUserList()
                reRegisterAlarms(context, userList)
            }

        }
    }

    private fun reRegisterAlarms(context: Context, userList: List<User>) {
        for (i in userList) {

            Notification.notificationID = i.notificationID

            // Title and Message
            val title = i.title
            val message = i.message
            val newTitle = "Today is ${i.name}'s Birthday!"

            // Making intent for broadcast receiver
            val intent = Intent(context, Notification::class.java)
            intent.putExtra(titleExtra, newTitle)
            intent.putExtra(messageExtra, message)


            // Pending intent for alarm manager
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                i.notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // scheduling alarm manager
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                getTime(i.minute, i.hour, i.day, i.month, i.year),
                pendingIntent
            )

        }


    }

    private fun getTime(minute: Int, hour: Int, day: Int, month: Int, year: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

}





