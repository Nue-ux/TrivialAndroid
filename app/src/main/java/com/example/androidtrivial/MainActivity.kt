package com.example.androidtrivial

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtrivial.R
import com.example.androidtrivial.RetrofitClient
import com.example.androidtrivial.adapter.QuestionsAdapter
import com.example.androidtrivial.data.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var questionsAdapter: QuestionsAdapter
    private val questionsList = mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewQuestions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        questionsAdapter = QuestionsAdapter(questionsList)
        recyclerView.adapter = questionsAdapter

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preguntas = RetrofitClient.apiService.getPreguntas()
                withContext(Dispatchers.Main) {
                    questionsList.clear()
                    questionsList.addAll(preguntas)
                    questionsAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}