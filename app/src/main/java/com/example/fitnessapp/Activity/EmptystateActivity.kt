package com.example.fitnessapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.Adapter.ActivityAdapter // Адаптер для списка активностей
import com.example.fitnessapp.database.AppDatabase // База данных приложения
import com.example.fitnessapp.Entity.ActivityEntity // Сущность активности
import com.example.fitnessapp.Fragment.ActivityFragment // Фрагмент активностей
import com.example.fitnessapp.Fragment.ProfileFragment // Фрагмент профиля
import com.example.fitnessapp.SManager.SessionManager // Менеджер сессии
import com.google.android.material.bottomnavigation.BottomNavigationView // Навигационное меню
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmptystateActivity : AppCompatActivity() {

    private lateinit var topTabSwitcher: View // Индикатор верхней навигации
    private lateinit var addActivityButton: View // Кнопка добавления активности
    private lateinit var recyclerView: RecyclerView // Список активностей
    private lateinit var emptyStateMessage: View // Сообщение об отсутствии данных
    private lateinit var adapter: ActivityAdapter // Адаптер для списка активностей
    private lateinit var db: AppDatabase // База данных
    private var allActivities: List<ActivityEntity> = listOf() // Все активности
    private lateinit var sessionManager: SessionManager // Менеджер сессии
    private lateinit var loggedInUsername: String // Логин текущего пользователя

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emptystate)

        db = AppDatabase.getDatabase(this) // Инициализация базы данных
        sessionManager = SessionManager(this) // Инициализация менеджера сессии
        loggedInUsername = sessionManager.getUserLogin() ?: "unknown_user" // Получение логина пользователя

        topTabSwitcher = findViewById(R.id.top_tab_indicator) // Верхняя навигация
        addActivityButton = findViewById(R.id.add_activity_button) // Кнопка добавления активности
        recyclerView = findViewById(R.id.recycler_view) // Список активностей
        emptyStateMessage = findViewById(R.id.empty_state_message) // Сообщение об отсутствии данных

        recyclerView.layoutManager = LinearLayoutManager(this) // Настройка RecyclerView
        adapter = ActivityAdapter(this, listOf()) // Инициализация адаптера
        recyclerView.adapter = adapter

        lifecycleScope.launch { // Загрузка всех активностей
            allActivities = withContext(Dispatchers.IO) {
                db.activityDao().getAllActivities() // Получение всех активностей из базы данных
            }
            updateRecyclerView(loggedInUsername) // Обновление списка активностей
        }

        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation) // Верхнее меню навигации
        topNavigationView.setOnItemSelectedListener { item -> // Обработка выбора вкладки
            when (item.itemId) {
                R.id.navigation_my_activity -> {
                    updateRecyclerView(loggedInUsername) // Показать активности текущего пользователя
                    true
                }
                R.id.navigation_users_activity -> {
                    updateRecyclerView(null) // Показать активности других пользователей
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) { // Добавление фрагмента активностей при первом запуске
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ActivityFragment(), "ActivityFragment")
                .commit()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation) // Нижнее меню навигации
        bottomNavigationView.setOnItemSelectedListener { item -> // Обработка выбора вкладки
            when (item.itemId) {
                R.id.navigation_activity -> {
                    switchFragment(ActivityFragment(), "ActivityFragment") // Переключение на фрагмент активностей
                    true
                }
                R.id.navigation_profile -> {
                    switchFragment(ProfileFragment(), "ProfileFragment") // Переключение на фрагмент профиля
                    true
                }
                else -> false
            }
        }

        addActivityButton.setOnClickListener { // Обработка нажатия кнопки добавления активности
            val intent = Intent(this, NewActivity::class.java) // Открытие экрана создания новой активности
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // Обработка результата добавления/изменения активности
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == RESULT_OK) { // Если активность успешно изменена
            lifecycleScope.launch {
                allActivities = withContext(Dispatchers.IO) {
                    db.activityDao().getAllActivities() // Обновление списка активностей
                }
                updateRecyclerView(loggedInUsername) // Обновление интерфейса
            }
        }
    }

    private fun showEmptyState(show: Boolean) { // Показать/скрыть сообщение об отсутствии данных
        if (show) {
            recyclerView.visibility = View.GONE
            emptyStateMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateMessage.visibility = View.GONE
        }
    }

    private fun updateRecyclerView(username: String?) { // Обновление списка активностей
        val filteredActivities = if (username != null) { // Фильтрация активностей по автору
            allActivities.filter { it.author == username }
        } else {
            allActivities.filter { it.author != loggedInUsername }
        }
        runOnUiThread { // Обновление UI в основном потоке
            if (filteredActivities.isEmpty()) { // Если список пуст
                showEmptyState(true)
            } else { // Если список не пуст
                showEmptyState(false)
                adapter = ActivityAdapter(this, filteredActivities) // Обновление адаптера
                recyclerView.adapter = adapter
            }
        }
    }

    private fun switchFragment(fragment: Fragment, tag: String) { // Переключение между фрагментами
        val fragmentManager = supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag(tag)
        fragmentManager.beginTransaction().apply {
            fragmentManager.fragments.forEach { hide(it) } // Скрытие всех фрагментов
            if (existingFragment != null) { // Если фрагмент уже существует
                show(existingFragment)
            } else { // Если фрагмент не существует
                add(R.id.fragment_container, fragment, tag)
            }
        }.commit()

        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        val addActivityButton = findViewById<View>(R.id.add_activity_button)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val emptyStateMessage = findViewById<View>(R.id.empty_state_message)

        if (tag == "ProfileFragment") { // Настройка интерфейса для фрагмента профиля
            topNavigationView.alpha = 0f
            addActivityButton.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyStateMessage.visibility = View.GONE
        } else { // Настройка интерфейса для фрагмента активностей
            topNavigationView.alpha = 1f
            addActivityButton.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            emptyStateMessage.visibility = View.GONE
        }
    }

    companion object {
        const val REQUEST_CODE_DETAIL = 1
    }
}