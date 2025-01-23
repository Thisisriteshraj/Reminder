package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.reminder.broadcastReceiver.Notification
import com.example.reminder.databinding.ActivityHomeBinding
import com.example.reminder.interfaces.OnItemClickOfUserUserBirthdayScheduled
import com.example.reminder.room.User
import com.example.reminder.room.UserDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Home : AppCompatActivity(), OnItemClickOfUserUserBirthdayScheduled {

    private lateinit var userDataBase: UserDataBase
    private var useralist: ArrayList<User> = ArrayList()

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userDataBase =
            Room.databaseBuilder(applicationContext, UserDataBase::class.java, "USER")
                .build()

        binding.floatingactionbtn.setOnClickListener {
            startActivity(Intent(this, AddNewPeople::class.java))
        }
    }

    @Override
    override fun onResume() {
        super.onResume()


        userDataBase.studentDAO().getAllUserList2().observe(this) {
            useralist.addAll(it)
            binding.recyclerView.adapter = AdapterPersonList(it as ArrayList, this)

        }




    }

    override fun onItemClick(user: User) {
        cancelNotification(user.notificationID)
        lifecycleScope.launch {
            userDataBase.studentDAO().deleteUserData(user)
            onResume()
        }
    }

    private fun cancelNotification(notificationId: Int) {
        Log.d("IDNOTIFICATION", "$notificationId")

//        // Cancel the notification
//        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.cancel(notificationId)

        // Cancel the alarm
        val intent = Intent(this, Notification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId, // Use the same notification ID as requestCode
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent) // Cancel the alarm
    }

}