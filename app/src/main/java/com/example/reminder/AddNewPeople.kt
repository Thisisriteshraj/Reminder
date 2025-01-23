package com.example.reminder

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.reminder.broadcastReceiver.Notification
import com.example.reminder.broadcastReceiver.Notification.Companion.messageExtra
import com.example.reminder.broadcastReceiver.Notification.Companion.titleExtra
import com.example.reminder.databinding.ActivityAddNewPeopleBinding
import com.example.reminder.model.DateAndTime
import com.example.reminder.room.User
import com.example.reminder.room.UserDataBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddNewPeople : AppCompatActivity() {

    private lateinit var userDataBase: UserDataBase
    private lateinit var binding: ActivityAddNewPeopleBinding
    private var nameOfPerson: String = ""
    private lateinit var dateAndTime: DateAndTime

    private var minute: Int = 0
    private var hour: Int = 0
    private var day = 0
    private var month = 0
    private var year = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPeopleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitButton.setOnClickListener {
            scheduleNotification()
        }

        userDataBase = Room.databaseBuilder(applicationContext, UserDataBase::class.java, "USER")
            .build()
    }

    private fun scheduleNotification() {

        // Generate unique requestCode for PendingIntent
        val randomNumber = generateUniqueRandomNumber()
        Notification.notificationID = randomNumber

        // Title and Message
        val title = binding.titleET.text.toString()
        val message = binding.messageET.text.toString()
        val newTitle = "Today is ${binding.Name.text.toString()}'s Birthday!"

        // Making intent for broadcast receiver
        val intent = Intent(this, Notification::class.java)
        intent.putExtra(titleExtra, newTitle)
        intent.putExtra(messageExtra, message)


        // Pending intent for alarm manager
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            randomNumber,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // scheduling alarm manager
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTime(), pendingIntent)


        // saving scheduled alarm in room
        GlobalScope.launch {
            userDataBase.studentDAO().insertUserData(
                User(
                    randomNumber, nameOfPerson, minute, hour, day, month, year, title, message
                )
            )
        }


        // showing dialog for notification in scheduled
        showAlert(getTime(), title, message, binding.Name.text.toString())

    }

    private fun showAlert(time: Long, title: String, message: String, name: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Name: $name\nTitle: $title\nMessage: $message\nAt: " +
                        dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    private fun getTime(): Long {
        minute = binding.timePicker.minute
        hour = binding.timePicker.hour
        day = binding.datePicker.dayOfMonth
        month = binding.datePicker.month
        year = binding.datePicker.year

        dateAndTime = DateAndTime(minute, hour, day, month, year)

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun generateUniqueRandomNumber(): Int {
        val currentDate = Date()
        val timeFormat = SimpleDateFormat("HHmmssSSS")
        return timeFormat.format(currentDate).toInt()
    }
}
