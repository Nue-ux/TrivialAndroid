package com.example.androidtrivial.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtrivial.R
import com.example.androidtrivial.data.Question

class QuestionsAdapter(private val questionsList: List<Question>) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewQuestion: TextView = itemView.findViewById(R.id.textViewQuestion)
        val textViewOptions: TextView = itemView.findViewById(R.id.textViewOptions)
        val textViewCorrect: TextView = itemView.findViewById(R.id.textViewCorrect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questionsList[position]
        holder.textViewQuestion.text = question.pregunta
        holder.textViewOptions.text = question.opciones.joinToString(separator = ", ")
        holder.textViewCorrect.text = question.correcta
    }

    override fun getItemCount(): Int = questionsList.size
}