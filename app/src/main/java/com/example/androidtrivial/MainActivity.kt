package com.example.androidtrivial

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtrivial.data.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var usuarioRegistrado: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextName: EditText = findViewById(R.id.editTextName)
        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        val recyclerViewQuestions: RecyclerView = findViewById(R.id.recyclerViewQuestions)

        recyclerViewQuestions.layoutManager = LinearLayoutManager(this)

        buttonRegister.setOnClickListener {
            val nombreUsuario = editTextName.text.toString().trim()
            if (nombreUsuario.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val nuevoUsuario = RetrofitClient.apiService.createUsuario(Usuario(0, nombreUsuario, 0))
                        usuarioRegistrado = nuevoUsuario
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "User registered: ${nuevoUsuario.nombre}",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Disable registration fields after success
                            editTextName.isEnabled = false
                            buttonRegister.isEnabled = false
                            // Navigate to QuizActivity after registration
                            val intent = Intent(this@MainActivity, QuizActivity::class.java)
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "Error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}