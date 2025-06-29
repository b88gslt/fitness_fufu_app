package com.example.fitnessapp

import android.app.Application
import android.util.Log
import com.example.fitnessapp.database.AppDatabase

class MyApp : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        Log.d("MyApp", "DB init: ${database.openHelper.databaseName}")
    }
}