package com.example.androidtrivial

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", 0)

        if (userId == 0) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
            return
        }

        val userName = sharedPref.getString("USER_NAME", "Usuario")
        val textViewWelcome: TextView = findViewById(R.id.textViewWelcome)
        val buttonStartQuiz: Button = findViewById(R.id.buttonStartQuiz)
        val buttonInfo: Button = findViewById(R.id.buttonInfo)
        val buttonNewInfo: Button = findViewById(R.id.buttonNewInfo)
        val buttonConfigOpcion : Button = findViewById(R.id.buttonOpcions)

        textViewWelcome.text = "Bienvenido, $userName"

        buttonStartQuiz.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        buttonInfo.setOnClickListener {
            startActivity(Intent(this, DifficultyActivity::class.java))
        }

        buttonNewInfo.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }

        buttonConfigOpcion.setOnClickListener {
            startActivity(Intent(this, OpcionsActivity::class.java))
        }


    }
}