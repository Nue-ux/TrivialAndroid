// File: app/src/main/java/com/example/androidtrivial/adapter/ScoreboardAdapter.kt
package com.example.androidtrivial.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtrivial.R
import com.example.androidtrivial.data.Usuario

class ScoreboardAdapter(private val usuarios: List<Usuario>) : RecyclerView.Adapter<ScoreboardAdapter.ScoreboardViewHolder>() {

    inner class ScoreboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewUserName: TextView = itemView.findViewById(R.id.textViewUserName)
        val textViewUserScore: TextView = itemView.findViewById(R.id.textViewUserScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scoreboard, parent, false)
        return ScoreboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreboardViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.textViewUserName.text = usuario.nombre
        holder.textViewUserScore.text = usuario.puntaje.toString()
    }

    override fun getItemCount(): Int = usuarios.size
}