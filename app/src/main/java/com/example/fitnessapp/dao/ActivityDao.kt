package com.example.fitnessapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fitnessapp.Entity.ActivityEntity

@Dao
interface ActivityDao {
    @Query("SELECT * FROM Activity ORDER BY startTime DESC")
    fun getAllActivities(): List<ActivityEntity>

    @Query("SELECT * FROM Activity WHERE id = :id")
    fun getActivityById(id: Int): ActivityEntity?

    @Insert
    fun insertActivities(vararg activities: ActivityEntity)

    @Query("DELETE FROM Activity")
    fun deleteAllActivities()

    @Query("UPDATE Activity SET comment = :comment WHERE id = :id")
    fun updateComment(id: Int, comment: String)

    @Query("DELETE FROM Activity WHERE id = :id")
    fun deleteActivityById(id: Int)
}