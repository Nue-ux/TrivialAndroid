package com.example.androidtrivial

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.androidtrivial.data.Question
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import android.graphics.drawable.Animatable
import android.util.Log

class QuizActivity : AppCompatActivity() {

    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var correctAnswers = 0

    private lateinit var textViewQuestion: TextView
    private lateinit var buttonOption1: Button
    private lateinit var buttonOption2: Button
    private lateinit var buttonOption3: Button
    private lateinit var buttonOption4: Button
    private lateinit var textViewScore: TextView
    private lateinit var textViewProgress: TextView
    private lateinit var imageViewAnimation: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        textViewQuestion = findViewById(R.id.textViewQuestion)
        buttonOption1 = findViewById(R.id.buttonOption1)
        buttonOption2 = findViewById(R.id.buttonOption2)
        buttonOption3 = findViewById(R.id.buttonOption3)
        buttonOption4 = findViewById(R.id.buttonOption4)
        textViewScore = findViewById(R.id.textViewScore)
        textViewProgress = findViewById(R.id.textViewProgress)
        imageViewAnimation = findViewById(R.id.imageViewAnimation)
        imageViewAnimation.post {
            showAnimation(R.drawable.anim_hacker_check)
        }


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedQuestions = RetrofitClient.apiService.getPreguntas()
                val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                val selectedCount = sharedPref.getInt("NUMBER_OF_QUESTIONS", 10)
                questions = if (fetchedQuestions.size > selectedCount) {
                    fetchedQuestions.subList(0, selectedCount)
                } else {
                    fetchedQuestions
                }
                withContext(Dispatchers.Main) {
                    updateProgress()
                    loadQuestion()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadQuestion() {
        if (currentQuestionIndex >= questions.size) {
            submitFinalScore()
            return
        }
        // Se reinician el fondo, si está habilitado y el color del texto a su valor original
        listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).forEach { button ->
            button.setBackgroundColor(Color.LTGRAY)
            button.isEnabled = true
            button.setTextColor(getColor(R.color.matrixGreen))
        }

        val question = questions[currentQuestionIndex]
        textViewQuestion.text = question.pregunta

        buttonOption1.text = question.opciones.getOrNull(0) ?: ""
        buttonOption2.text = question.opciones.getOrNull(1) ?: ""
        buttonOption3.text = question.opciones.getOrNull(2) ?: ""
        buttonOption4.text = question.opciones.getOrNull(3) ?: ""

        buttonOption1.setOnClickListener { checkAnswer(buttonOption1, question) }
        buttonOption2.setOnClickListener { checkAnswer(buttonOption2, question) }
        buttonOption3.setOnClickListener { checkAnswer(buttonOption3, question) }
        buttonOption4.setOnClickListener { checkAnswer(buttonOption4, question) }
    }

    private fun checkAnswer(selectedButton: Button, question: Question) {
        listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).forEach { it.isEnabled = false }
        val selectedAnswer = selectedButton.text.toString()

        if (selectedAnswer == question.correcta) {
            selectedButton.setBackgroundColor(Color.GREEN)
            selectedButton.setTextColor(Color.WHITE)
            correctAnswers++
            showAnimation(R.drawable.anim_hacker_check)
            // Actualiza la puntuación usando la función updateScore
            CoroutineScope(Dispatchers.IO).launch {
                val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                val userId = sharedPref.getInt("USER_ID", 0)
                if (userId != 0) {
                    try {
                        RetrofitClient.apiService.updateScore(userId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            selectedButton.setBackgroundColor(Color.RED)
            selectedButton.setTextColor(Color.WHITE)
            listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).find {
                it.text.toString() == question.correcta
            }?.setBackgroundColor(Color.GREEN)
            showAnimation(R.drawable.anim_hacker_cross)
        }
        updateProgress()

        Handler(Looper.getMainLooper()).postDelayed({
            imageViewAnimation.visibility = ImageView.GONE
            currentQuestionIndex++
            loadQuestion()
        }, 2000)
    }

    private fun showAnimation(@DrawableRes resId: Int) {
        val drawable = AnimatedVectorDrawableCompat.create(this, resId)
        if (drawable == null) {
            Log.e("QUIZ", "No se pudo inflar el AVD")
            return                    // <‑‑ salimos antes de hacer nada
        }
        imageViewAnimation.setImageDrawable(drawable)
        imageViewAnimation.visibility = View.VISIBLE
        (drawable as Animatable).start()
    }


    private fun updateProgress() {
        textViewScore.text = "Aciertos: $correctAnswers"
        textViewProgress.text = "Pregunta: ${currentQuestionIndex + 1}/${questions.size}"
    }

    private fun submitFinalScore() {
        val intent = Intent(this, ScoreboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}