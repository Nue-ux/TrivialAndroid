// Language: kotlin
package com.example.androidtrivial

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.androidtrivial.data.Question

class QuizActivity : AppCompatActivity() {

    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0

    private lateinit var textViewQuestion: TextView
    private lateinit var buttonOption1: Button
    private lateinit var buttonOption2: Button
    private lateinit var buttonOption3: Button
    private lateinit var buttonOption4: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        textViewQuestion = findViewById(R.id.textViewQuestion)
        buttonOption1 = findViewById(R.id.buttonOption1)
        buttonOption2 = findViewById(R.id.buttonOption2)
        buttonOption3 = findViewById(R.id.buttonOption3)
        buttonOption4 = findViewById(R.id.buttonOption4)

        // Carga las preguntas desde la API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedQuestions = RetrofitClient.apiService.getPreguntas()
                // Recuperar el número de preguntas de las preferencias compartidas
                val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                val numberOfQuestions = sharedPref.getInt("NUMBER_OF_QUESTIONS", fetchedQuestions.size)
                questions = if (fetchedQuestions.size > numberOfQuestions) {
                    fetchedQuestions.subList(0, numberOfQuestions)
                } else {
                    fetchedQuestions
                }
                withContext(Dispatchers.Main) {
                    loadQuestion()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadQuestion() {
        // Si se han agotado todas las preguntas, envía la puntuación final
        if (currentQuestionIndex >= questions.size) {
            submitFinalScore()
            return
        }

        // Botones de opción
        listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).forEach { button ->
            button.setBackgroundColor(Color.LTGRAY)
            button.isEnabled = true
        }

        val question = questions[currentQuestionIndex]
        textViewQuestion.text = question.pregunta

        // Establecer opciones de respuesta
        val options = question.opciones
        buttonOption1.text = options.getOrNull(0) ?: ""
        buttonOption2.text = options.getOrNull(1) ?: ""
        buttonOption3.text = options.getOrNull(2) ?: ""
        buttonOption4.text = options.getOrNull(3) ?: ""

        // Enlace de clics para verificar la respuesta
        buttonOption1.setOnClickListener { checkAnswer(buttonOption1, options, question) }
        buttonOption2.setOnClickListener { checkAnswer(buttonOption2, options, question) }
        buttonOption3.setOnClickListener { checkAnswer(buttonOption3, options, question) }
        buttonOption4.setOnClickListener { checkAnswer(buttonOption4, options, question) }
    }

    private fun checkAnswer(selectedButton: Button, options: List<String>, question: Question) {
        // Deshabilitar todos los botones de opción
        listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).forEach { it.isEnabled = false }

        val selectedAnswer = selectedButton.text.toString()
        if (selectedAnswer == question.correcta) {
            selectedButton.setBackgroundColor(Color.GREEN)
            // Llama a la API para actualizar la puntuación del usuario
            val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
            val userId = sharedPref.getInt("USER_ID", 0)
            if (userId != 0) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitClient.apiService.updateScore(userId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            selectedButton.setBackgroundColor(Color.RED)
            // Indicar la respuesta correcta
            listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).find {
                it.text.toString() == question.correcta
            }?.setBackgroundColor(Color.GREEN)
        }
        // Después de 2 segundos, carga la siguiente pregunta
        Handler(Looper.getMainLooper()).postDelayed({
            currentQuestionIndex++
            loadQuestion()
        }, 2000)
    }

    private fun submitFinalScore() {
        // Redirige a la actividad ScoreboardActivity
        val intent = Intent(this, ScoreboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}