package com.desarrolloaplicaciones.sazon


import com.desarrolloaplicaciones.sazon.RecetaModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Header

interface ApiService {
    @GET("recetas/ultimas")
    suspend fun obtenerRecetas(): List<RecetaModel>

    @GET("api/recetas/{recetaId}")
    suspend fun obtenerRecetaPorId(@Path("recetaId") recetaId: String): RecetaDetalle

    @GET("api/comentarios/receta/{recetaId}")
    suspend fun obtenerComentariosPorReceta(@Path("recetaId") recetaId: String): ComentarioResponse

    @POST("api/comentarios")
    suspend fun crearComentario(@Body comentario: ComentarioRequest): ComentarioModel

    @GET("api/recetas/{recetaID}/imagenes")
    suspend fun obtenerImagenReceta(@Path("recetaID") recetaID: String): ImagenRecetaResponse

}