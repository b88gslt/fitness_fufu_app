package com.example.fitnessapp.Activity

import android.os.Bundle // Для сохранения состояния активности
import android.widget.Button // Кнопка
import android.widget.EditText // Поле ввода текста
import android.widget.RadioButton // Радиокнопка
import android.widget.RadioGroup // Группа радиокнопок
import androidx.appcompat.app.AppCompatActivity // Базовый класс для активностей
import androidx.lifecycle.lifecycleScope // Корутина для асинхронных операций
import com.example.fitnessapp.R // Файл ресурсов
import com.example.fitnessapp.Entity.UserEntity // Сущность пользователя
import com.example.fitnessapp.database.UserDatabase // База данных пользователей
import kotlinx.coroutines.launch // Запуск корутины

class RegisterActivity : AppCompatActivity() {

    private lateinit var userDatabase: UserDatabase // База данных пользователей

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_form) // Установка макета активности

        userDatabase = UserDatabase.getDatabase(this) // Инициализация базы данных

        val loginEditText = findViewById<EditText>(R.id.login_edittext) // Поле для ввода логина
        val usernameEditText = findViewById<EditText>(R.id.username_edittext) // Поле для ввода имени пользователя
        val passwordEditText = findViewById<EditText>(R.id.password_edittext) // Поле для ввода пароля
        val genderGroup = findViewById<RadioGroup>(R.id.gender_group) // Группа радиокнопок для выбора пола

        findViewById<Button>(R.id.register_button).setOnClickListener { // Обработка нажатия кнопки "Зарегистрироваться"
            val login = loginEditText.text.toString() // Получение логина
            val username = usernameEditText.text.toString() // Получение имени пользователя
            val password = passwordEditText.text.toString() // Получение пароля
            val gender = findViewById<RadioButton>(genderGroup.checkedRadioButtonId).text.toString() // Получение выбранного пола

            lifecycleScope.launch { // Запуск корутины для записи данных в базу
                val user = UserEntity( // Создание объекта пользователя
                    login = login,
                    username = username,
                    password = password,
                    gender = gender
                )
                userDatabase.userDao().insert(user)
            }
        }
    }
}