// Language: kotlin
package com.example.androidtrivial

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtrivial.adapter.ScoreboardAdapter
import com.example.androidtrivial.data.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoreboardActivity : AppCompatActivity() {

    private lateinit var recyclerViewScoreboard: RecyclerView
    private lateinit var buttonPlayAgain: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        recyclerViewScoreboard = findViewById(R.id.recyclerViewScoreboard)
        buttonPlayAgain = findViewById(R.id.buttonPlayAgain)
        recyclerViewScoreboard.layoutManager = LinearLayoutManager(this)

        buttonPlayAgain.setOnClickListener {
            // Redriger al usuario a la pantalla de juego
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Recupera la tabla de puntuaciones desde la API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scoreboard = RetrofitClient.apiService.getScoreboard()
                withContext(Dispatchers.Main) {
                    recyclerViewScoreboard.adapter = ScoreboardAdapter(scoreboard)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}