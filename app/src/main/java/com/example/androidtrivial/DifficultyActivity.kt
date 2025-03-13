package com.example.androidtrivial

import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DifficultyActivity : AppCompatActivity() {

    private lateinit var numberPickerQuestions: NumberPicker
    private lateinit var buttonResetScore: Button
    private lateinit var buttonSave: Button

    // Recupera el ID de usuario pasado
    // El valor predeterminado es 0 si no está disponible
    private val userId: Int by lazy { intent.getIntExtra("USER_ID", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_difficulty)

        numberPickerQuestions = findViewById(R.id.numberPickerQuestions)
        buttonResetScore = findViewById(R.id.buttonResetScore)
        buttonSave = findViewById(R.id.buttonSave)

        // Configuración: número, rango del selector y valor predeterminado

        numberPickerQuestions.minValue = 5
        numberPickerQuestions.maxValue = 20
        numberPickerQuestions.value = 10

        buttonResetScore.setOnClickListener {
            if (userId == 0) {
                Toast.makeText(this, "User not registered.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Llama a la API para restablecer la puntuación del ID de usuario especificado
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.apiService.updateScore(userId)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DifficultyActivity,
                            "Score reset successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DifficultyActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        buttonSave.setOnClickListener {
            val selectedNumber = numberPickerQuestions.value
            // Guarde el número de preguntas elegido en las preferencias compartidas
            val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putInt("NUMBER_OF_QUESTIONS", selectedNumber)
                apply()
            }
            Toast.makeText(
                this,
                "Number of questions set to $selectedNumber",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
}