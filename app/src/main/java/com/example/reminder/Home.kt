package com.example.reminder

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.reminder.AddNewPeople.Companion.cancelNotification
import com.example.reminder.databinding.ActivityHomeBinding
import com.example.reminder.interfaces.OnItemClickOfUserUserBirthdayScheduled
import com.example.reminder.room.User
import com.example.reminder.room.UserDataBase
import kotlinx.coroutines.launch

class Home : AppCompatActivity(), OnItemClickOfUserUserBirthdayScheduled {

    private lateinit var userDataBase: UserDataBase
    private var useralist: ArrayList<User> = ArrayList()

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingactionbtn.setOnClickListener {
            startActivity(Intent(this, AddNewPeople::class.java))
        }
    }

    @Override
    override fun onResume() {
        super.onResume()

        userDataBase =
            Room.databaseBuilder(applicationContext, UserDataBase::class.java, "USER")
                .build()
        userDataBase.studentDAO().getAllUserList().observe(this) {
            useralist.addAll(it)
            binding.recyclerView.adapter = AdapterPersonList(it as ArrayList, this)

        }

    }

    override fun onItemClick(user: User) {
        cancelNotification(user.notificationID,this)
        lifecycleScope.launch {
            userDataBase.studentDAO().deleteUserData(user)
            onResume()

        }
    }

//    private fun cancelNotification(notificationId: Int) {
//        Log.d("IDNOTIFICATION","$notificationId")
//
//        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.cancel(notificationId) // Cancel the notification
//    }

}