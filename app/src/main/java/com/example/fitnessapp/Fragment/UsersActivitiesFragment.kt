package com.example.fitnessapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.Adapter.ActivityAdapter
import com.example.fitnessapp.database.AppDatabase
import com.example.fitnessapp.Entity.ActivityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersActivitiesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter
    private lateinit var db: AppDatabase
    private var allActivities: List<ActivityEntity> = listOf()
    private lateinit var loggedInUsername: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users_activities, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        db = AppDatabase.getDatabase(requireContext())
        loggedInUsername = "User3"

        GlobalScope.launch {
            allActivities = withContext(Dispatchers.IO) {
                db.activityDao().getAllActivities()
            }
            val filteredActivities = allActivities.filter { it.author != loggedInUsername }
            withContext(Dispatchers.Main) {
                adapter = ActivityAdapter(requireContext(), filteredActivities)
                recyclerView.adapter = adapter
            }
        }

        return view
    }
}