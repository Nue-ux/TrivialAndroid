package com.example.androidtrivial

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.androidtrivial.data.Question
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import android.graphics.drawable.Animatable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat

class QuizActivity : AppCompatActivity() {

    private val REQUEST_READ_PHONE_STATE = 100

    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var correctAnswers = 0

    private lateinit var textViewQuestion: TextView
    private lateinit var buttonOption1: Button
    private lateinit var buttonOption2: Button
    private lateinit var buttonOption3: Button
    private lateinit var buttonOption4: Button
    private lateinit var textViewScore: TextView
    private lateinit var textViewProgress: TextView
    private lateinit var imageViewAnimation: ImageView
    private lateinit var bgMusic: MediaPlayer

    private lateinit var sharedPref: SharedPreferences
    private lateinit var soundPool: SoundPool
    private val soundMapCorrect = mutableMapOf<Int, Int>()
    private val soundMapWrong = mutableMapOf<Int, Int>()
    private val soundMapFinish = mutableMapOf<Int, Int>()
    private val soundLoaded = mutableMapOf<Int, Boolean>()
    private var selectedFinish = 0
    private var selectedCorrect = 0
    private var selectedWrong = 0

    // Variables para gestionar el estado de la llamada
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        textViewQuestion = findViewById(R.id.textViewQuestion)
        buttonOption1 = findViewById(R.id.buttonOption1)
        buttonOption2 = findViewById(R.id.buttonOption2)
        buttonOption3 = findViewById(R.id.buttonOption3)
        buttonOption4 = findViewById(R.id.buttonOption4)
        textViewScore = findViewById(R.id.textViewScore)
        textViewProgress = findViewById(R.id.textViewProgress)
        imageViewAnimation = findViewById(R.id.imageViewAnimation)

        sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
        selectedCorrect = sharedPref.getInt("SOUND_CORRECT", 0)
        selectedWrong = sharedPref.getInt("SOUND_WRONG", 0)

        bgMusic = MediaPlayer.create(this, R.raw.gamefond)

        bgMusic.isLooping = true
        if (isSoundEnabled()) {
            bgMusic.start()
        }

        // Inicializar SoundPool para reproducir sonidos de acierto y fallo.
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { pool, sampleId, status ->
            if (status == 0) {
                soundLoaded[sampleId] = true
                Log.d("QuizActivity", "Sonido cargado: $sampleId")
            }
        }

        // Cargar sonidos de acierto y fallo
        soundMapCorrect[0] = soundPool.load(this, R.raw.correct, 1)
        soundMapCorrect[1] = soundPool.load(this, R.raw.correct2, 1)
        soundMapCorrect[2] = soundPool.load(this, R.raw.correct3, 1)

        soundMapWrong[0] = soundPool.load(this, R.raw.wrong, 1)
        soundMapWrong[1] = soundPool.load(this, R.raw.wrong2, 1)
        soundMapWrong[2] = soundPool.load(this, R.raw.wrong3, 1)

        // Cargar sonidos de finalización
        soundMapFinish[0] = soundPool.load(this, R.raw.finish1, 1)
        soundMapFinish[1] = soundPool.load(this, R.raw.finish2, 1)
        soundMapFinish[2] = soundPool.load(this, R.raw.finish3, 1)

        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING,
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        if (bgMusic.isPlaying) {
                            val pos = bgMusic.currentPosition
                            sharedPref.edit().putInt("MUSIC_POSITION", pos).apply()
                            bgMusic.pause()
                        }
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        val pos = sharedPref.getInt("MUSIC_POSITION", 0)
                        bgMusic.seekTo(pos)
                        if (!bgMusic.isPlaying && isSoundEnabled()) {
                            bgMusic.start()
                        }
                    }
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_READ_PHONE_STATE
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedQuestions = RetrofitClient.apiService.getPreguntas()
                val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                val selectedCount = sharedPref.getInt("NUMBER_OF_QUESTIONS", 10)
                questions = if (fetchedQuestions.size > selectedCount) {
                    fetchedQuestions.subList(0, selectedCount)
                } else {
                    fetchedQuestions
                }
                withContext(Dispatchers.Main) {
                    updateProgress()
                    loadQuestion()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_PHONE_STATE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private fun playCorrectSound() {
        val soundId = soundMapCorrect[selectedCorrect] ?: soundMapCorrect[0]!!
        // Verificar si el sonido se cargó
        if (soundLoaded[soundId] == true) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        } else {
            Log.d("QuizActivity", "El sonido aún no está cargado")
        }
    }

    private fun displayQuestion(question: Question) {
        textViewQuestion.text = question.pregunta
        val options = question.opciones
        buttonOption1.text = options.getOrNull(0) ?: ""
        buttonOption2.text = options.getOrNull(1) ?: ""
        buttonOption3.text = options.getOrNull(2) ?: ""
        buttonOption4.text = options.getOrNull(3) ?: ""
        textViewProgress.text = "Pregunta: ${currentQuestionIndex + 1}/${questions.size}"
    }

    private fun finishQuiz() {
        // 1. Sonido de fin
        selectedFinish = sharedPref.getInt("SOUND_FINISH", 0)
        val finishSoundId = soundMapFinish[selectedFinish] ?: soundMapFinish[0]!!

        if (isSoundEnabled()) {
            soundPool.play(finishSoundId, 1f, 1f, 1, 0, 1f)
        }
        // 2. Espera ~1s para que se oiga y luego navega
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ScoreboardActivity::class.java))
            finish()           // cierra la Activity actual
        }, 1000)               // ajusta a la duración real de tu .wav
    }


    private fun loadQuestion() {
        if (currentQuestionIndex >= questions.size) {
            finishQuiz()
            return
        }
        // Se reinician el fondo, si está habilitado y el color del texto a su valor original
        listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            button.isEnabled = true
            button.setTextColor(ContextCompat.getColor(this, R.color.matrixGreen))
        }

        val question = questions[currentQuestionIndex]
        textViewQuestion.text = question.pregunta

        buttonOption1.text = question.opciones.getOrNull(0) ?: ""
        buttonOption2.text = question.opciones.getOrNull(1) ?: ""
        buttonOption3.text = question.opciones.getOrNull(2) ?: ""
        buttonOption4.text = question.opciones.getOrNull(3) ?: ""

        buttonOption1.setOnClickListener { checkAnswer(buttonOption1, question) }
        buttonOption2.setOnClickListener { checkAnswer(buttonOption2, question) }
        buttonOption3.setOnClickListener { checkAnswer(buttonOption3, question) }
        buttonOption4.setOnClickListener { checkAnswer(buttonOption4, question) }
    }

    private fun checkAnswer(selectedButton: Button, question: Question) {
        listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).forEach {
            it.isEnabled = false
        }
        val selectedAnswer = selectedButton.text.toString()

        if (selectedAnswer == question.correcta) {
            selectedButton.setBackgroundColor(Color.GREEN)
            selectedButton.setTextColor(Color.WHITE)
            correctAnswers++

            // ► reproducir sonido de acierto
            val soundId = soundMapCorrect[selectedCorrect] ?: soundMapCorrect[0]!!
            if (isSoundEnabled()) {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }

            if (isEffectsEnabled()) {
                showAnimation(R.drawable.anim_hacker_check)
            }

            // Actualiza la puntuación usando la función updateScore
            CoroutineScope(Dispatchers.IO).launch {
                val sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
                val userId = sharedPref.getInt("USER_ID", 0)
                if (userId != 0) {
                    try {
                        RetrofitClient.apiService.updateScore(userId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            selectedButton.setBackgroundColor(Color.RED)
            selectedButton.setTextColor(Color.WHITE)
            val soundId = soundMapWrong[selectedWrong] ?: soundMapWrong[0]!!
            if (isSoundEnabled()) {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }

            listOf(buttonOption1, buttonOption2, buttonOption3, buttonOption4).find {
                it.text.toString() == question.correcta
            }?.setBackgroundColor(Color.GREEN)

            if (isEffectsEnabled()) {
                showAnimation(R.drawable.anim_hacker_cross)
            }
        }


        updateProgress()

        Handler(Looper.getMainLooper()).postDelayed({
            imageViewAnimation.visibility = ImageView.GONE
            currentQuestionIndex++
            loadQuestion()
        }, 2000)
    }

    private fun showAnimation(@DrawableRes resId: Int) {
        // 1.Inflar el Animated Vector Drawable con el recurso que llega por parámetro
        val drawable = AnimatedVectorDrawableCompat.create(this, resId)
            ?: run {
                Log.e("QUIZ", "No se pudo inflar el AVD (¿falta algún color?)")
                return          // si es null abortamos
            }

        // 2.Asignarlo al ImageView (asegúrate de que el ImageView tiene constraints y tamaño)
        imageViewAnimation.setImageDrawable(drawable)
        imageViewAnimation.visibility = View.VISIBLE

        // 3.Arrancar la animación
        (drawable as Animatable).start()
    }

    override fun onPause() {
        super.onPause()
        val pos = bgMusic.currentPosition
        getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
            .edit()
            .putInt("MUSIC_POSITION", pos)
            .apply()
        if (bgMusic.isPlaying) {
            bgMusic.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        val pos = getSharedPreferences("MyGamePrefs", MODE_PRIVATE)
            .getInt("MUSIC_POSITION", 0)
        bgMusic.seekTo(pos)
        if (!bgMusic.isPlaying && isSoundEnabled()) {
            bgMusic.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        soundPool.release()
        bgMusic.release()
    }

    private fun updateProgress() {
        textViewScore.text = "Aciertos: $correctAnswers"
        textViewProgress.text = "Pregunta: ${currentQuestionIndex + 1}/${questions.size}"
    }

    private fun isSoundEnabled(): Boolean =
        sharedPref.getBoolean("SOUND_ENABLED", true)

    private fun isEffectsEnabled(): Boolean =
        sharedPref.getBoolean("EFFECTS_ENABLED", true)

    private fun submitFinalScore() {
        val intent = Intent(this, ScoreboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}