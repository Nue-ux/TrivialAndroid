package com.example.androidtrivial

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

class EfectosFragment : Fragment() {

    private lateinit var switchEffects: Switch
    private lateinit var buttonSaveEffects: Button
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_efectos, container, false)
        sharedPref = requireActivity().getSharedPreferences("MyGamePrefs", AppCompatActivity.MODE_PRIVATE)
        switchEffects = view.findViewById(R.id.switchEffects)
        buttonSaveEffects = view.findViewById(R.id.buttonSaveEffects)

        // Restaurar configuración guardada
        switchEffects.isChecked = sharedPref.getBoolean("EFFECTS_ENABLED", true)

        buttonSaveEffects.setOnClickListener {
            with(sharedPref.edit()){
                putBoolean("EFFECTS_ENABLED", switchEffects.isChecked)
                apply()
            }
            Toast.makeText(context, "Configuración de efectos guardada", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}