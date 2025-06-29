package com.example.fitnessapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.Activity.ActivityDetailActivity
import com.example.fitnessapp.Entity.ActivityEntity
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter(private val context: Context, private val activities: List<ActivityEntity>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.bind(activity)
    }

    override fun getItemCount(): Int = activities.size

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeTextView: TextView = itemView.findViewById(R.id.activity_type)
        private val distanceTextView: TextView = itemView.findViewById(R.id.activity_distance)
        private val timeTextView: TextView = itemView.findViewById(R.id.activity_time)
        private val authorTextView: TextView = itemView.findViewById(R.id.activity_author)
        private val dateTextView: TextView = itemView.findViewById(R.id.activity_date_detail)
        private val activityDateTextView: TextView = itemView.findViewById(R.id.activity_date)

        fun bind(activity: ActivityEntity) {
            typeTextView.text = activity.type
            distanceTextView.text = formatDistance(activity.distanceInMeters.toString())
            timeTextView.text = formatTime(activity.timeInSeconds.toString())
            authorTextView.text = "@${activity.author}"
            dateTextView.text = activity.date
            activityDateTextView.text = formatActivityDate(activity.date)

            itemView.setOnClickListener {
                val intent = Intent(context, ActivityDetailActivity::class.java)
                intent.putExtra("activity_id", activity.id)
                context.startActivity(intent)
            }
        }

        private fun formatDistance(distance: String): String {
            val distanceInMeters = distance.replace(" м", "").toInt()
            return if (distanceInMeters >= 1000) {
                String.format("%.1f км", distanceInMeters / 1000.0)
            } else {
                "$distanceInMeters м"
            }
        }

        private fun formatTime(time: String): String {
            val timeInMinutes = time.replace(" мин", "").toInt()
            return if (timeInMinutes >= 60) {
                String.format("%.1f ч", timeInMinutes / 60.0)
            } else {
                "$timeInMinutes мин"
            }
        }

        private fun formatActivityDate(date: String): String {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val activityDate = sdf.parse(date)
            val currentDate = Calendar.getInstance().time

            val diff = currentDate.time - (activityDate?.time ?: 0)
            val daysDiff = diff / (1000 * 60 * 60 * 24)

            return when {
                daysDiff == 0L -> "Сегодня"
                daysDiff == 1L -> "Вчера"
                daysDiff == 2L -> "Позавчера"
                else -> SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
                    activityDate ?: Date()
                )
            }
        }
    }
}