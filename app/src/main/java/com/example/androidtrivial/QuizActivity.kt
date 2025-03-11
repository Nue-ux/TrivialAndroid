package com.example.androidtrivial

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtrivial.data.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // Load questions from API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                questions = RetrofitClient.apiService.getPreguntas()
                withContext(Dispatchers.Main) {
                    loadQuestion()
                }
            } catch (e: Exception) {
                // Optionally manage error state here
                e.printStackTrace()
            }
        }
    }

    private fun loadQuestion() {
        if (currentQuestionIndex >= questions.size) {
            finish()  // End quiz if all questions are answered
            return
        }

        // Reset button colors and enable them
        listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).forEach { button ->
            button.setBackgroundColor(Color.LTGRAY)
            button.isEnabled = true
        }

        val question = questions[currentQuestionIndex]
        textViewQuestion.text = question.pregunta

        // Assuming exactly four options for simplicity
        val options = question.opciones
        buttonOption1.text = options.getOrNull(0) ?: ""
        buttonOption2.text = options.getOrNull(1) ?: ""
        buttonOption3.text = options.getOrNull(2) ?: ""
        buttonOption4.text = options.getOrNull(3) ?: ""

        // Setup click listeners
        buttonOption1.setOnClickListener { checkAnswer(buttonOption1, options, question) }
        buttonOption2.setOnClickListener { checkAnswer(buttonOption2, options, question) }
        buttonOption3.setOnClickListener { checkAnswer(buttonOption3, options, question) }
        buttonOption4.setOnClickListener { checkAnswer(buttonOption4, options, question) }
    }

    private fun checkAnswer(selectedButton: Button, options: List<String>, question: Question) {
        // Disable all buttons
        listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).forEach { it.isEnabled = false }

        // Check if selected text matches the correct answer
        val selectedAnswer = selectedButton.text.toString()
        if (selectedAnswer == question.correcta) {
            selectedButton.setBackgroundColor(Color.GREEN)
        } else {
            selectedButton.setBackgroundColor(Color.RED)
            // Mark correct answer green
            listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).find {
                it.text.toString() == question.correcta
            }?.setBackgroundColor(Color.GREEN)
        }
        // Delay and load next question
        Handler(Looper.getMainLooper()).postDelayed({
            currentQuestionIndex++
            loadQuestion()
        }, 2000)
    }
}