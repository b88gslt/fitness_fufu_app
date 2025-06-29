package com.example.fitnessapp.Activity

import android.content.Intent // Для работы с намерениями (Intent)
import android.os.Bundle // Для сохранения состояния активности
import android.text.SpannableString // Для форматирования текста
import android.text.Spanned // Интерфейс для форматированного текста
import android.text.style.ForegroundColorSpan // Для изменения цвета текста
import android.widget.* // Виджеты Android
import androidx.appcompat.app.AppCompatActivity // Базовый класс для активностей
import androidx.core.content.ContextCompat // Для получения ресурсов
import androidx.lifecycle.lifecycleScope // Корутина для асинхронных операций
import com.example.fitnessapp.R // Файл ресурсов
import com.example.fitnessapp.Entity.UserEntity // Сущность пользователя
import com.example.fitnessapp.SManager.SessionManager // Менеджер сессии
import com.example.fitnessapp.database.UserDatabase // База данных пользователей
import kotlinx.coroutines.launch // Запуск корутины

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userDatabase: UserDatabase // База данных пользователей
    private lateinit var sessionManager: SessionManager // Менеджер сессии

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_form) // Установка макета активности

        val loginField = findViewById<EditText>(R.id.login_edittext) // Поле для ввода логина
        val usernameField = findViewById<EditText>(R.id.username_edittext) // Поле для ввода имени пользователя
        val passwordField = findViewById<EditText>(R.id.password_edittext) // Поле для ввода пароля
        val repeatPasswordField = findViewById<EditText>(R.id.repeat_password_edittext) // Поле для повтора пароля
        val genderGroup = findViewById<RadioGroup>(R.id.gender_group) // Группа радиокнопок для выбора пола
        val registerButton = findViewById<Button>(R.id.register_button) // Кнопка "Зарегистрироваться"
        val backArrow = findViewById<ImageView>(R.id.back_arrow) // Кнопка "Назад"

        userDatabase = UserDatabase.getDatabase(this) // Инициализация базы данных
        sessionManager = SessionManager(this) // Инициализация менеджера сессии

        backArrow.setOnClickListener { // Обработка нажатия кнопки "Назад"
            navigateToMainActivity() // Переход на главный экран
        }

        registerButton.setOnClickListener { // Обработка нажатия кнопки "Зарегистрироваться"
            val login = loginField.text.toString().trim() // Получение логина
            val username = usernameField.text.toString().trim() // Получение имени пользователя
            val password = passwordField.text.toString().trim() // Получение пароля
            val repeatPassword = repeatPasswordField.text.toString().trim() // Получение повторного пароля
            val genderId = genderGroup.checkedRadioButtonId // ID выбранного пола

            if (login.isEmpty() || username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || genderId == -1) { // Проверка заполнения полей
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = findViewById<RadioButton>(genderId)?.text.toString() // Получение выбранного пола

            if (password == repeatPassword) { // Проверка совпадения паролей
                lifecycleScope.launch { // Запуск корутины для проверки и записи данных
                    val existingUser = userDatabase.userDao().findUserByLogin(login) // Поиск пользователя по логину
                    if (existingUser != null) { // Если пользователь уже существует
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Логин уже существует",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else { // Если пользователь не существует
                        val user = UserEntity( // Создание нового пользователя
                            login = login,
                            username = username,
                            password = password,
                            gender = gender
                        )
                        userDatabase.userDao().insert(user) // Сохранение пользователя в базу данных
                        sessionManager.createLoginSession(login) // Создание сессии
                        navigateToEmptystateActivity() // Переход на следующий экран
                    }
                }
            } else {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            }
        }

        setupAgreementText()
    }

    private fun navigateToMainActivity() { // Переход на главный экран
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToEmptystateActivity() { // Переход на экран пустого состояния
        val intent = Intent(this, EmptystateActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupAgreementText() { // Настройка текста соглашения
        val agreementTextView = findViewById<TextView>(R.id.agreement_text) // Текст соглашения
        val text = getString(R.string.agree_text) // Получение строки из ресурсов
        val spannableString = SpannableString(text) // Создание форматируемой строки
        val purpleColor = ContextCompat.getColor(this, R.color.purple) // Получение цвета

        val policyText = "политикой конфиденциальности"
        val agreementText = "пользовательское соглашение"

        val policyStart = text.indexOf(policyText) // Начало подстроки "политика конфиденциальности"
        val policyEnd = policyStart + policyText.length // Конец подстроки
        val agreementStart = text.indexOf(agreementText) // Начало подстроки "пользовательское соглашение"
        val agreementEnd = agreementStart + agreementText.length // Конец подстроки

        if (policyStart != -1 && agreementStart != -1) { // Если найдены обе подстроки
            spannableString.setSpan( // Изменение цвета для "политики конфиденциальности"
                ForegroundColorSpan(purpleColor),
                policyStart,
                policyEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan( // Изменение цвета для "пользовательского соглашения"
                ForegroundColorSpan(purpleColor),
                agreementStart,
                agreementEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        agreementTextView.text = spannableString // Применение форматированного текста
    }
}