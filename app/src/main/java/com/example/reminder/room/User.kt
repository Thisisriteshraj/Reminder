package com.example.reminder.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "USER")
data class User(
    @PrimaryKey
    var notificationID: Int = 0,
    var name: String = "",
    val minute: Int = 0,
    val hour: Int = 0
)
