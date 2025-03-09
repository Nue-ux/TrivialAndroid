package com.example.androidtrivial.service

import com.example.androidtrivial.data.Question
import retrofit2.http.GET

interface ApiService {
    @GET("api/preguntas")
    suspend fun getPreguntas(): List<Question>
}