package com.example.fitnessapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fitnessapp.Entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE login = :login AND password = :password LIMIT 1")
    suspend fun findUserByLoginAndPassword(login: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun findUserByLogin(login: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("UPDATE users SET password = :newPassword WHERE login = :login")
    suspend fun updatePassword(login: String, newPassword: String)
}