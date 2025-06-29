package com.example.fitnessapp.Activity

import android.content.Intent // Для работы с намерениями (Intent)
import android.os.Bundle // Для сохранения состояния активности
import android.os.Handler // Для управления таймером
import android.os.Looper // Для работы с главным потоком
import android.view.View // Для работы с видимостью элементов
import android.widget.Button // Кнопка
import android.widget.LinearLayout // Линейный контейнер
import android.widget.TextView // Текстовое поле
import android.widget.Toast // Всплывающее сообщение
import androidx.appcompat.app.AppCompatActivity // Базовый класс для активностей
import androidx.constraintlayout.widget.ConstraintLayout // Ограничивающий макет
import androidx.recyclerview.widget.LinearLayoutManager // Менеджер компоновки RecyclerView
import androidx.recyclerview.widget.RecyclerView // Список элементов
import com.example.fitnessapp.R // Файл ресурсов
import com.example.fitnessapp.Adapter.ActivityTypeAdapter // Адаптер для типов активностей
import com.example.fitnessapp.database.AppDatabase // База данных приложения
import com.example.fitnessapp.Entity.ActivityEntity // Сущность активности
import com.example.fitnessapp.Entity.ActivityType // Сущность типа активности
import com.example.fitnessapp.SManager.SessionManager // Менеджер сессии
import kotlinx.coroutines.CoroutineScope // Область корутины
import kotlinx.coroutines.Dispatchers // Диспетчеры корутин
import kotlinx.coroutines.launch // Запуск корутины
import kotlinx.coroutines.withContext // Переключение контекста выполнения

class NewActivity : AppCompatActivity() {

    private lateinit var activityMenu: ConstraintLayout // Меню выбора типа активности
    private lateinit var activityInfoMenu: LinearLayout // Меню информации о активности
    private lateinit var activityTypeText: TextView // Текст типа активности
    private lateinit var activityDistanceText: TextView // Текст расстояния
    private lateinit var activityTimeText: TextView // Текст времени
    private lateinit var startButton: Button // Кнопка "Старт"
    private lateinit var stopButton: Button // Кнопка "Стоп"
    private var selectedActivityType: String? = null // Выбранный тип активности
    private var distance = 0 // Пройденное расстояние
    private var seconds = 0 // Прошедшее время в секундах
    private val handler = Handler(Looper.getMainLooper()) // Обработчик для таймера
    private lateinit var runnable: Runnable // Задача для таймера
    private lateinit var db: AppDatabase // База данных
    private lateinit var sessionManager: SessionManager // Менеджер сессии
    private lateinit var author: String // Автор активности

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new) // Установка макета активности

        db = AppDatabase.getDatabase(this) // Инициализация базы данных
        sessionManager = SessionManager(this) // Инициализация менеджера сессии
        author = sessionManager.getUserLogin() ?: "unknown_user" // Получение логина пользователя

        activityMenu = findViewById(R.id.activity_menu) // Меню выбора типа активности
        activityInfoMenu = findViewById(R.id.activity_info_menu) // Меню информации о активности
        activityTypeText = findViewById(R.id.activity_type) // Текст типа активности
        activityDistanceText = findViewById(R.id.activity_distance) // Текст расстояния
        activityTimeText = findViewById(R.id.activity_time) // Текст времени
        startButton = findViewById(R.id.start_button) // Кнопка "Старт"
        stopButton = findViewById(R.id.stop_button) // Кнопка "Стоп"

        val activityTypes = listOf( // Список доступных типов активностей
            ActivityType(1, "Велосипед"),
            ActivityType(2, "Бег"),
            ActivityType(3, "Шаг")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.activity_recycler_view) // Список типов активностей
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) // Настройка RecyclerView
        recyclerView.adapter = ActivityTypeAdapter(this, activityTypes) { activityType -> // Адаптер для списка типов активностей
            selectedActivityType = activityType.name // Сохранение выбранного типа активности
            activityTypeText.text = selectedActivityType // Обновление текста типа активности
        }

        startButton.setOnClickListener { // Обработка нажатия кнопки "Старт"
            if (selectedActivityType != null) { // Проверка, выбран ли тип активности
                activityMenu.visibility = View.GONE // Скрытие меню выбора типа активности
                activityInfoMenu.visibility = View.VISIBLE // Показ меню информации о активности
                activityDistanceText.text = "0 метров" // Обнуление расстояния
                activityTimeText.text = "00:00:00" // Обнуление времени
                startStopwatch() // Запуск секундомера
            } else { // Если тип активности не выбран
                Toast.makeText(this, "Выберите тип активности", Toast.LENGTH_SHORT).show() // Уведомление
            }
        }

        stopButton.setOnClickListener { // Обработка нажатия кнопки "Стоп"
            stopStopwatch()
        }
    }

    private fun startStopwatch() { // Запуск секундомера
        distance = 0
        seconds = 0
        runnable = object : Runnable { // Задача для таймера
            override fun run() {
                seconds++
                if (seconds % 2 == 0) { // Каждые 2 секунды увеличиваем расстояние
                    distance += 10
                    activityDistanceText.text = "$distance метров" // Обновление текста расстояния
                }
                activityTimeText.text = formatTime(seconds) // Обновление текста времени
                handler.postDelayed(this, 1000) // Повтор через 1 секунду
            }
        }
        handler.post(runnable) // Запуск задачи
    }

    private fun stopStopwatch() { // Остановка секундомера
        handler.removeCallbacks(runnable) // Остановка задачи
        saveActivityToDatabase() // Сохранение активности в базу данных
    }

    private fun saveActivityToDatabase() { // Сохранение активности в базу данных
        val activityType = selectedActivityType ?: return // Получение выбранного типа активности
        val currentTime = System.currentTimeMillis() // Текущее время
        val activityEntity = ActivityEntity( // Создание объекта активности
            id = 0,
            type = activityType,
            distanceInMeters = distance,
            timeInSeconds = seconds,
            startTime = currentTime - (seconds * 1000),
            endTime = currentTime,
            date = getCurrentDate(),
            author = author,
            comment = null
        )
        CoroutineScope(Dispatchers.IO).launch { // Запуск корутины для записи в базу данных
            db.activityDao().insertActivities(activityEntity) // Сохранение активности
            withContext(Dispatchers.Main) { // Переключение на главный поток
                navigateToEmptystateActivity() // Переход на экран пустого состояния
            }
        }
    }

    private fun navigateToEmptystateActivity() { // Переход на экран пустого состояния
        val intent = Intent(this, EmptystateActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun formatTime(seconds: Int): String { // Форматирование времени
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}