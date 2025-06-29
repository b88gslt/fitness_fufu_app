package com.example.fitnessapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.Entity.ActivityType

class ActivityTypeAdapter(
    private val context: Context,
    private val activityTypes: List<ActivityType>,
    private val onItemClick: (ActivityType) -> Unit
) : RecyclerView.Adapter<ActivityTypeAdapter.ActivityTypeViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityTypeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_activity_type, parent, false)
        return ActivityTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityTypeViewHolder, position: Int) {
        val activityType = activityTypes[position]
        holder.bind(activityType, position == selectedPosition)
    }

    override fun getItemCount(): Int = activityTypes.size

    inner class ActivityTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeTextView: TextView = itemView.findViewById(R.id.activity_type_text)
        private val typeImageView: ImageView = itemView.findViewById(R.id.activity_type_image)

        fun bind(activityType: ActivityType, isSelected: Boolean) {
            typeTextView.text = activityType.name
            val imageResId = when (activityType.name) {
                "Велосипед" -> R.drawable.activity_image
                "Бег" -> R.drawable.activity_image
                "Шаг" -> R.drawable.activity_image
                else -> R.drawable.activity_image
            }
            typeImageView.setImageResource(imageResId)
            itemView.isSelected = isSelected
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClick(activityType)
            }
        }
    }
}