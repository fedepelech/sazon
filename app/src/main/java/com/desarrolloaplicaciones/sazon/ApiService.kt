package com.desarrolloaplicaciones.sazon


import com.desarrolloaplicaciones.sazon.RecetaModel
import retrofit2.http.GET

interface ApiService {
    @GET("recetas/ultimas")
    suspend fun obtenerRecetas(): List<RecetaModel>
}