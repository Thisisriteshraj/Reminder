package com.example.reminder.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDAO {

    @Query("SELECT * FROM  USER")
     fun getAllUserList(): LiveData<List<User>>

    @Insert
    suspend fun insertUserData(user: User)

    @Delete
    suspend fun deleteUserData(user: User)

}