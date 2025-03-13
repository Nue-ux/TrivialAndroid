// Language: kotlin
package com.example.androidtrivial

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        buttonRegister.setOnClickListener {
            val nombreUsuario = editTextName.text.toString().trim()
            if (nombreUsuario.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val usuarios = RetrofitClient.apiService.getScoreboard()
                        val usuarioExiste = usuarios.any { it.nombre.equals(nombreUsuario, ignoreCase = true) }

                        withContext(Dispatchers.Main) {
                            if (usuarioExiste) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "User already registered.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Registra el nuevo usuario
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
                                            editTextName.isEnabled = false
                                            buttonRegister.isEnabled = false
                                            // Guarda el ID de usuario en SharedPreferences
                                            val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                                            sharedPref.edit().putInt("USER_ID", nuevoUsuario.id).apply()
                                            // Navega a la actividad HomeActivity después de registrar al usuario
                                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                            startActivity(intent)
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Error registering user: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "Error checking user: ${e.message}",
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