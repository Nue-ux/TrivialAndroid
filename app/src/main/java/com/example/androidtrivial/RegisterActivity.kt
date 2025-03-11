// Language: Kotlin
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

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val editTextName: EditText = findViewById(R.id.editTextName)
        val buttonRegister: Button = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val userName = editTextName.text.toString().trim()
            if (userName.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val newUser = RetrofitClient.apiService.createUsuario(Usuario(0, userName, 0))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity,
                                "User registered: ${newUser.nombre}",
                                Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finish()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity,
                                "Error: ${e.message}",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}