package com.idanrayan.instantmessagesusingnearby.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT name FROM users WHERE device_id =:id")
    suspend fun getUser(id: String): String

    @Query("SELECT name FROM users WHERE device_id IN (:registeredUsers)")
    fun getRegisteredUsers(registeredUsers: List<String>): LiveData<List<String>>

    @Query("SELECT * FROM users WHERE name LIKE :name")
    suspend fun findByName(name: String): User

    // Replace user if is conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)
}
