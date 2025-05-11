package com.example.androidtrivial.service

import com.example.androidtrivial.data.Question
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.example.androidtrivial.data.Usuario
import retrofit2.http.Body

interface ApiService {
    @GET("api/preguntas")
    suspend fun getPreguntas(): List<Question>

    @POST("api/usuarios")
    suspend fun createUsuario(@Body usuario: Usuario): Usuario

    @PUT("api/usuarios/{id}/score")
    suspend fun updateScore(@Path("id") id: Int): Usuario

    @PUT("api/usuarios/{id}/puntos")
    suspend fun resetScore(@Path("id") id: Int): Usuario

    @GET("api/usuarios/scoreboard")
    suspend fun getScoreboard(): List<Usuario>
}