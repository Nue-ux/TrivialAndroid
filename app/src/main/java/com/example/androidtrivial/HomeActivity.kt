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
            // Abre la pantalla de informacion
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        buttonDifficulty.setOnClickListener {
            // Abre la pantalla de dificultad
            val intent = Intent(this, DifficultyActivity::class.java)
            startActivity(intent)
        }

        buttonStartGame.setOnClickListener {
            // Initia el juego
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }
}