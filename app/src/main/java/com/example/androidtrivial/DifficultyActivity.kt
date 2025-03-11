// Language: kotlin
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

    // Retrieve the user id passed via Intent extras; default is 0 if not available
    private val userId: Int by lazy { intent.getIntExtra("USER_ID", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_difficulty)

        numberPickerQuestions = findViewById(R.id.numberPickerQuestions)
        buttonResetScore = findViewById(R.id.buttonResetScore)
        buttonSave = findViewById(R.id.buttonSave)

        // Setup number picker range and default value
        numberPickerQuestions.minValue = 5
        numberPickerQuestions.maxValue = 20
        numberPickerQuestions.value = 10

        buttonResetScore.setOnClickListener {
            if (userId == 0) {
                Toast.makeText(this, "User not registered.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Call API to reset the score for the specified user id
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
            // Save the chosen number of questions in shared preferences
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
            finish() // Return to the previous screen
        }
    }
}