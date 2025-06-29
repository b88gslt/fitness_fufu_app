package com.example.fitnessapp.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fitnessapp.R
import com.example.fitnessapp.database.UserDatabase // База данных пользователей
import com.example.fitnessapp.SManager.SessionManager // Менеджер сессии
import com.example.fitnessapp.dao.UserDao // DAO для работы с пользователями
import com.google.android.material.textfield.TextInputEditText // Поля ввода текста
import com.google.android.material.button.MaterialButton // Кнопка подтверждения
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager // Менеджер сессии для получения текущего пользователя
    private lateinit var userDao: UserDao // DAO для работы с пользователями

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        sessionManager = SessionManager(this) // Инициализация менеджера сессии
        userDao = UserDatabase.getDatabase(this).userDao() // Получение доступа к базе данных пользователей

        val oldPasswordEditText = findViewById<TextInputEditText>(R.id.old_password_edittext) // Поле для старого пароля
        val newPasswordEditText = findViewById<TextInputEditText>(R.id.new_password_edittext) // Поле для нового пароля
        val repeatNewPasswordEditText = findViewById<TextInputEditText>(R.id.repeat_new_password_edittext) // Поле для повтора нового пароля
        val agreeChangesButton = findViewById<MaterialButton>(R.id.agree_changes_button) // Кнопка подтверждения изменений

        agreeChangesButton.setOnClickListener { // Обработка нажатия на кнопку подтверждения
            val oldPassword = oldPasswordEditText.text.toString() // Старый пароль
            val newPassword = newPasswordEditText.text.toString() // Новый пароль
            val repeatNewPassword = repeatNewPasswordEditText.text.toString() // Повтор нового пароля

            if (newPassword == repeatNewPassword) { // Проверка совпадения новых паролей
                lifecycleScope.launch { // Запуск корутины для выполнения операции
                    val login = sessionManager.getUserLogin() // Получение логина текущего пользователя
                    val user = login?.let { userDao.findUserByLoginAndPassword(it, oldPassword) } // Поиск пользователя по логину и старому паролю

                    if (user != null) { // Если пользователь найден
                        updatePassword(login, newPassword) // Обновление пароля в базе данных
                        Toast.makeText(this@ChangePasswordActivity, "Пароль изменён", Toast.LENGTH_SHORT).show() // Уведомление об успешном изменении
                        finish() // Закрытие активности
                    } else { // Если пользователь не найден
                        Toast.makeText(this@ChangePasswordActivity, "Старый пароль неправильный", Toast.LENGTH_SHORT).show() // Уведомление о неверном старом пароле
                    }
                }
            } else { // Если новые пароли не совпадают
                Toast.makeText(this, "Новые пароли не совпадают", Toast.LENGTH_SHORT).show() // Уведомление о несовпадении паролей
            }
        }
    }

    private suspend fun updatePassword(login: String, newPassword: String) { // Функция для обновления пароля
        withContext(Dispatchers.IO) { // Выполнение операции в фоновом потоке
            userDao.updatePassword(login, newPassword) // Обновление пароля в базе данных
        }
    }
}