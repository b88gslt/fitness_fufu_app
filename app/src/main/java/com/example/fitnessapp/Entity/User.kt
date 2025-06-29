package com.example.fitnessapp.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val login: String,
    val username: String,
    val password: String,
    val gender: String
)