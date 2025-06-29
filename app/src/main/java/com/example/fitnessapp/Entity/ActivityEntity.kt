package com.example.fitnessapp.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Activity")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val type: String,
    val distanceInMeters: Int,
    val timeInSeconds: Int,
    val startTime: Long,
    val endTime: Long,
    val date: String,
    val author: String,
    val comment: String?
)