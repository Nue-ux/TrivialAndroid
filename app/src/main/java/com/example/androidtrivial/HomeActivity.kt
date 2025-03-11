package com.example.androidtrivial

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import kotlin.jvm.java

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val buttonInfo: Button = findViewById(R.id.buttonInfo)
        val buttonDifficulty: Button = findViewById(R.id.buttonDifficulty)
        val buttonStartGame: Button = findViewById(R.id.buttonStartGame)

        buttonInfo.setOnClickListener {
            // Open the info screen (replace with your target class)
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        buttonDifficulty.setOnClickListener {
            // Open the difficulty settings screen (replace with your target class)
            val intent = Intent(this, DifficultyActivity::class.java)
            startActivity(intent)
        }

        buttonStartGame.setOnClickListener {
            // Start the game screen (replace with your game activity class)
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }
}