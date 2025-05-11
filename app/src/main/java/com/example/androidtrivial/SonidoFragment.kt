package com.example.androidtrivial

import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class SonidoFragment : Fragment() {

    private lateinit var listViewCorrect: ListView
    private lateinit var listViewWrong: ListView
    private lateinit var listViewFinish: ListView
    private lateinit var buttonSaveSound: Button
    private lateinit var sharedPref: SharedPreferences
    private lateinit var soundPool: SoundPool
    private lateinit var buttonMuteSound: Button

    private val soundMapCorrect = mutableMapOf<Int, Int>()
    private val soundMapWrong = mutableMapOf<Int, Int>()
    private val soundMapFinish = mutableMapOf<Int, Int>()

    private var selectedCorrect = 0
    private var selectedWrong = 0
    private var selectedFinish = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_sonido, container, false)

        // 1) Referencias a vistas
        listViewCorrect = view.findViewById(R.id.listViewCorrect)
        listViewWrong   = view.findViewById(R.id.listViewWrong)
        listViewFinish  = view.findViewById(R.id.listViewFinish)
        buttonSaveSound = view.findViewById(R.id.buttonSaveSound)
        buttonMuteSound = view.findViewById(R.id.buttonMuteSound)

        // 2) Preferencias
        sharedPref = requireActivity()
            .getSharedPreferences("MyGamePrefs", AppCompatActivity.MODE_PRIVATE)

        // 3) Texto inicial del botón mute
        updateMuteButtonText()

        // 4) Variables persistidas
        selectedCorrect = sharedPref.getInt("SOUND_CORRECT", 0)
        selectedWrong   = sharedPref.getInt("SOUND_WRONG",   0)
        selectedFinish  = sharedPref.getInt("SOUND_FINISH",  0)

        // Inicializar SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(3).setAudioAttributes(audioAttributes).build()

        // Cargar sonidos
        soundMapCorrect[0] = soundPool.load(context, R.raw.correct, 1)
        soundMapCorrect[1] = soundPool.load(context, R.raw.correct2, 1)
        soundMapCorrect[2] = soundPool.load(context, R.raw.correct3, 1)

        soundMapWrong[0] = soundPool.load(context, R.raw.wrong, 1)
        soundMapWrong[1] = soundPool.load(context, R.raw.wrong2, 1)
        soundMapWrong[2] = soundPool.load(context, R.raw.wrong3, 1)

        soundMapFinish[0] = soundPool.load(context, R.raw.finish1, 1)
        soundMapFinish[1] = soundPool.load(context, R.raw.finish2, 1)
        soundMapFinish[2] = soundPool.load(context, R.raw.finish3, 1)

        // Arreglos con los nombres de cada sonido para mostrar en la lista
        val correctSoundNames = listOf("Sonido Correcto 1", "Sonido Correcto 2", "Sonido Correcto 3")
        val wrongSoundNames = listOf("Sonido Fallo 1", "Sonido Fallo 2", "Sonido Fallo 3")
        val finishSoundNames = listOf("Sonido Finalización 1", "Sonido Finalización 2", "Sonido Finalización 3")

        // Configurar adaptadores usando el layout personalizado item_sonido.xml
        val adapterCorrect = ArrayAdapter(requireContext(), R.layout.item_sonido, correctSoundNames)
        listViewCorrect.adapter = adapterCorrect
        listViewCorrect.setOnItemClickListener { _, _, position, _ ->
            selectedCorrect = position
            Toast.makeText(context, "Seleccionado: ${correctSoundNames[position]}", Toast.LENGTH_SHORT).show()

            if (sharedPref.getBoolean("SOUND_ENABLED", true)) {
                soundPool.play(soundMapCorrect[position] ?: 0, 1f, 1f, 1, 0, 1f)
            }
        }

        val adapterWrong = ArrayAdapter(requireContext(), R.layout.item_sonido, wrongSoundNames)
        listViewWrong.adapter = adapterWrong
        listViewWrong.setOnItemClickListener { _, _, position, _ ->
            selectedWrong = position
            Toast.makeText(context, "Seleccionado: ${wrongSoundNames[position]}", Toast.LENGTH_SHORT).show()
            if (sharedPref.getBoolean("SOUND_ENABLED", true)) {
                soundPool.play(soundMapWrong[position] ?: 0, 1f, 1f, 1, 0, 1f)
            }
        }

        val adapterFinish = ArrayAdapter(requireContext(), R.layout.item_sonido, finishSoundNames)
        listViewFinish.adapter = adapterFinish
        listViewFinish.setOnItemClickListener { _, _, position, _ ->
            selectedFinish = position
            Toast.makeText(context, "Seleccionado: ${finishSoundNames[position]}", Toast.LENGTH_SHORT).show()
            if (sharedPref.getBoolean("SOUND_ENABLED", true)) {
                soundPool.play(soundMapFinish[position] ?: 0, 1f, 1f, 1, 0, 1f)
            }
        }

        buttonSaveSound.setOnClickListener {
            with(sharedPref.edit()) {
                putInt("SOUND_CORRECT", selectedCorrect)
                putInt("SOUND_WRONG", selectedWrong)
                putInt("SOUND_FINISH", selectedFinish)
                apply()
            }
            Toast.makeText(context, "Configuración de sonido guardada", Toast.LENGTH_SHORT).show()
        }

        // Botón para alternar sonido global
        buttonMuteSound.setOnClickListener {
            val enabled = sharedPref.getBoolean("SOUND_ENABLED", true)
            sharedPref.edit().putBoolean("SOUND_ENABLED", !enabled).apply()

            updateMuteButtonText()

            Toast.makeText(
                context,
                if (!enabled) "Sonido activado" else "Sonido desactivado",
                Toast.LENGTH_SHORT
            ).show()
        }

        return view
    }

    private fun updateMuteButtonText() {
        val enabled = sharedPref.getBoolean("SOUND_ENABLED", true)
        buttonMuteSound.text =
            if (enabled) "Desactivar sonido" else "Activar sonido"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        soundPool.release()
    }
}