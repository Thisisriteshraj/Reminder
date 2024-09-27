package com.example.reminder

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.room.Room
import com.example.reminder.broadcastReceiver.Notification
import com.example.reminder.broadcastReceiver.Notification.Companion.messageExtra
import com.example.reminder.broadcastReceiver.Notification.Companion.notificationID
import com.example.reminder.broadcastReceiver.Notification.Companion.titleExtra
import com.example.reminder.databinding.ActivityAddNewPeopleBinding
import com.example.reminder.model.DateAndTime
import com.example.reminder.room.User
import com.example.reminder.room.UserDataBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AddNewPeople : AppCompatActivity() {

    private lateinit var userDataBase: UserDataBase
    private lateinit var binding: ActivityAddNewPeopleBinding
    private var nameOfPerson: String = ""
    private var cuttent: Int = 0
    private var useralist: ArrayList<User> = ArrayList()
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

        userDataBase =
            Room.databaseBuilder(applicationContext, UserDataBase::class.java, "USER")
                .build()

        userDataBase.studentDAO().getAllUserList().observe(this, Observer { it ->
            useralist.addAll(it)



            if (useralist.size == 0) {
                cuttent = 0


            } else {
                cuttent = useralist[useralist.size - 1].notificationID + 1

            }

        })


    }

    private fun scheduleNotification() {

        //
        val title = binding.titleET.text.toString()
        val message = binding.messageET.text.toString()
        val newTitle = "Today is ${binding.Name.text.toString()}'s Birthday!"

        //
        val intent = Intent(this, Notification::class.java)
        intent.putExtra(titleExtra, newTitle)
        intent.putExtra(messageExtra, message)


        notificationID = cuttent
        // Generate unique requestCode for PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            cuttent,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTime(), pendingIntent)


        //
        nameOfPerson = binding.Name.text.toString()
//        if (nameOfPerson.isNotEmpty()) {
//            Home.addPeople(nameOfPerson)
//        }


        GlobalScope.launch {


            userDataBase.studentDAO().insertUserData(
                User(
                    notificationID, nameOfPerson, minute, hour

                )
            )

        }



        showAlert(getTime(), title, message, nameOfPerson)


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

    companion object {
        fun cancelNotification(notificationId: Int, context: Context) {
            val notificationManager = getSystemService(context, NotificationManager::class.java)
            notificationManager?.cancel(notificationId) // Cancel the notification
        }

    }
}
