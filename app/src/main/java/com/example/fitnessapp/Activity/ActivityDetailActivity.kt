package com.example.fitnessapp.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fitnessapp.R
import com.example.fitnessapp.database.AppDatabase
import com.example.fitnessapp.SManager.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ActivityDetailActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var sessionManager: SessionManager
    private var activityId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        db = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        val deleteActivityButton = findViewById<Button>(R.id.delete_activity_button)
        val activityType = findViewById<TextView>(R.id.activity_detail_type)
        val activityDistance = findViewById<TextView>(R.id.activity_detail_distance)
        val activityTime = findViewById<TextView>(R.id.activity_detail_time)
        val activityTimeAgo = findViewById<TextView>(R.id.activity_detail_time_ago)
        val activityStartTime = findViewById<TextView>(R.id.activity_detail_start_time)
        val activityEndTime = findViewById<TextView>(R.id.activity_detail_end_time)
        val commentEditText = findViewById<EditText>(R.id.comment_edit_text)

        activityId = intent.getIntExtra("activity_id", -1)

        lifecycleScope.launch {
            val activity = withContext(Dispatchers.IO) {
                db.activityDao().getActivityById(activityId)
            }
            activity?.let {
                activityType.text = it.type
                activityDistance.text = formatDistance(it.distanceInMeters)
                activityTime.text = formatTime(it.timeInSeconds)
                activityTimeAgo.text = formatTimeAgo(it.startTime)
                activityStartTime.text = formatStartTime(it.startTime)
                activityEndTime.text = formatEndTime(it.endTime)
                commentEditText.setText(it.comment ?: "")

                val loggedInUser = sessionManager.getUserLogin()
                if (loggedInUser == it.author) {
                    commentEditText.isEnabled = true
                    deleteActivityButton.isEnabled = true
                    commentEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            val newComment = s.toString()
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    db.activityDao().updateComment(activityId, newComment)
                                }
                                Toast.makeText(
                                    this@ActivityDetailActivity,
                                    "Комментарий обновлён",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
                } else {
                    commentEditText.isEnabled = false
                    deleteActivityButton.isEnabled = false
                }
            }

            backArrow.setOnClickListener {
                finish()
            }

            deleteActivityButton.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.activityDao().deleteActivityById(activityId)
                    }
                    Toast.makeText(
                        this@ActivityDetailActivity,
                        "Запись удалена",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun formatDistance(distanceInMeters: Int): String {
        return if (distanceInMeters >= 1000) {
            String.format("%.2f км", distanceInMeters / 1000.0)
        } else {
            "$distanceInMeters м"
        }
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return if (hours > 0) {
            String.format("%d ч %02d мин", hours, minutes)
        } else {
            String.format("%d мин", minutes)
        }
    }

    private fun formatTimeAgo(startTime: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - startTime
        val hours = diff / (1000 * 60 * 60)
        return "$hours часов назад"
    }

    private fun formatStartTime(startTime: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return "Старт ${sdf.format(Date(startTime))}"
    }

    private fun formatEndTime(endTime: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return "Финиш ${sdf.format(Date(endTime))}"
    }
}