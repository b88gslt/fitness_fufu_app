package com.example.fitnessapp.Activity

import android.content.Intent // Для работы с намерениями (Intent)
import android.os.Bundle // Для сохранения состояния активности
import android.widget.Button // Кнопка
import androidx.appcompat.app.AppCompatActivity // Базовый класс для активностей
import com.example.fitnessapp.R // Файл ресурсов
import com.example.fitnessapp.SManager.SessionManager // Менеджер сессии

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager // Менеджер сессии

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Установка макета активности

        sessionManager = SessionManager(this) // Инициализация менеджера сессии

        val alreadyHaveAccountButton = findViewById<Button>(R.id.already_have_account_button) // Кнопка "У меня уже есть аккаунт"
        alreadyHaveAccountButton.setOnClickListener { // Обработка нажатия кнопки
            val intent = Intent(this, LoginActivity::class.java) // Создание намерения для перехода на экран входа
            startActivity(intent) // Запуск активности входа
        }

        val registerButton: Button = findViewById(R.id.register_button) // Кнопка "Зарегистрироваться"
        registerButton.setOnClickListener { // Обработка нажатия кнопки
            val intent = Intent(this, RegistrationActivity::class.java) // Создание намерения для перехода на экран регистрации
            startActivity(intent) // Запуск активности регистрации
        }
    }
}