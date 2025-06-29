package com.example.fitnessapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button // Кнопка
import android.widget.EditText // Поле ввода текста
import android.widget.ImageView // Изображение
import android.widget.Toast // Всплывающее сообщение
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // Корутина для асинхронных операций
import com.example.fitnessapp.R
import com.example.fitnessapp.SManager.SessionManager // Менеджер сессии
import com.example.fitnessapp.database.UserDatabase // База данных пользователей
import kotlinx.coroutines.launch // Запуск корутины

class LoginActivity : AppCompatActivity() {

    private lateinit var userDatabase: UserDatabase // База данных пользователей
    private lateinit var sessionManager: SessionManager // Менеджер сессии

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Установка макета активности

        val loginField = findViewById<EditText>(R.id.login_edittext) // Поле для ввода логина
        val passwordField = findViewById<EditText>(R.id.password_edittext) // Поле для ввода пароля
        val loginButton = findViewById<Button>(R.id.login_button) // Кнопка входа
        val backArrow = findViewById<ImageView>(R.id.back_arrow) // Кнопка "Назад"

        userDatabase = UserDatabase.getDatabase(this) // Инициализация базы данных
        sessionManager = SessionManager(this) // Инициализация менеджера сессии

        backArrow.setOnClickListener { // Обработка нажатия кнопки "Назад"
            navigateToMainActivity() // Переход на главный экран
        }

        loginButton.setOnClickListener { // Обработка нажатия кнопки входа
            val login = loginField.text.toString().trim() // Получение логина
            val password = passwordField.text.toString().trim() // Получение пароля

            if (login.isEmpty() || password.isEmpty()) { // Проверка заполнения полей
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch { // Запуск корутины для проверки пользователя
                val user = userDatabase.userDao().findUserByLoginAndPassword(login, password) // Поиск пользователя в базе данных
                if (user != null) { // Если пользователь найден
                    sessionManager.createLoginSession(login) // Создание сессии
                    navigateToEmptystateActivity() // Переход на следующий экран
                } else { // Если пользователь не найден
                    Toast.makeText(
                        this@LoginActivity,
                        "Неверный логин или пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun navigateToEmptystateActivity() { // Переход на экран пустого состояния
        val intent = Intent(this, EmptystateActivity::class.java)
        startActivity(intent)
        finish() // Завершение текущей активности
    }

    private fun navigateToMainActivity() { // Переход на главный экран
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}