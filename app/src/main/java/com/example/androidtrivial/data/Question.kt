package com.example.androidtrivial.data

data class Question(
    val id: Int,
    val pregunta: String,
    val opciones: List<String>,
    val correcta: String
)
