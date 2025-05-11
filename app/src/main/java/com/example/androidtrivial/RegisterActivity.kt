package com.example.androidtrivial

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtrivial.data.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout

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
                        val newUser =
                            RetrofitClient.apiService.createUsuario(Usuario(0, userName, 0))
                        withContext(Dispatchers.Main) {
                            // Guardar el id y nombre que retorna la API
                            val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putInt("USER_ID", newUser.id)
                                putString("USER_NAME", newUser.nombre)
                                apply()
                            }
                            Toast.makeText(
                                this@RegisterActivity,
                                "User registered: ${newUser.nombre}",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finish()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegisterActivity,
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

        val imageView = findViewById<ImageView>(R.id.androidWave)
        val drawable = imageView.drawable

        if (drawable is AnimatedVectorDrawable) {
            drawable.start()
        } else {
            imageView.setImageResource(R.drawable.android_wave_anim)
            (imageView.drawable as? AnimatedVectorDrawable)?.start()
        }

        startMatrixAnimation()
    }

    private fun startMatrixAnimation() {
        val container = findViewById<FrameLayout>(R.id.matrixContainer)
        val characters = "01"
        val screenWidth = resources.displayMetrics.widthPixels
        val columnCount = 20
        val charSize = screenWidth / columnCount

        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                repeat(columnCount) { column ->
                    val textView = TextView(this@RegisterActivity).apply {
                        text = characters.random().toString()
                        setTextColor(getColor(R.color.matrixGreen))
                        textSize = 18f
                        typeface = android.graphics.Typeface.MONOSPACE
                        gravity = Gravity.CENTER
                    }
                    val params = FrameLayout.LayoutParams(
                        charSize,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.leftMargin = column * charSize
                    container.addView(textView, params)

                    val animator = ObjectAnimator.ofFloat(
                        textView,
                        "translationY",
                        -100f,
                        container.height.toFloat() + 100
                    )
                    animator.duration = 2000L + (0..1000).random()
                    animator.interpolator = LinearInterpolator()
                    animator.start()

                    animator.addUpdateListener {
                        if (textView.translationY >= container.height) {
                            container.removeView(textView)
                        }
                    }
                }
                kotlinx.coroutines.delay(150L)
            }
        }
    }
}