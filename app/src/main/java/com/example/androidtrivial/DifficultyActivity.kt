package com.example.androidtrivial

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
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

    private val userId: Int by lazy { intent.getIntExtra("USER_ID", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_difficulty)

        numberPickerQuestions = findViewById(R.id.numberPickerQuestions)
        buttonResetScore = findViewById(R.id.buttonResetScore)
        buttonSave = findViewById(R.id.buttonSave)

        numberPickerQuestions.minValue = 5
        numberPickerQuestions.maxValue = 20
        numberPickerQuestions.value = 10

        buttonResetScore.setOnClickListener {
            if (userId == 0) {
                Toast.makeText(this, "User not registered.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Se utiliza la función resetScore en lugar de updateScore
                    RetrofitClient.apiService.resetScore(userId)
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

        val imageView = findViewById<ImageView>(R.id.androidWave)
        val drawable = imageView.drawable

        if (drawable is AnimatedVectorDrawable) {
            drawable.start()
        } else {
            imageView.setImageResource(R.drawable.android_wave_anim)
            (imageView.drawable as? AnimatedVectorDrawable)?.start()
        }
    }
}