package com.desarrolloaplicaciones.sazon.data

import com.google.gson.annotations.SerializedName

data class LoginReturn(
    val token: String
)

data class Recipe (
    val id: String,
    val nombre: String,
    val imageRes: Int,
    val createdAt: String
)

data class RecentRecipeReturn2 (
    val id: String,
    val nombre: String,
    @SerializedName("created_at") val createdAt: String
)

/*data class RecentRecipesResponse(
    val success: Boolean,
    val count: Int,
    val limite: Int,
    @SerializedName("ultimas_recetas") val ultimasRecetas: List<RecentRecipeReturn>,
    val message: String
)*/

data class RecentRecipeReturn(
    val id: String,
    val nombre: String,
    val descripcion: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("tipo_receta_id") val tipoRecetaId: Int,
    @SerializedName("nivel_dificultad_id") val nivelDificultadId: Int,
    val autor: Autor,
    val valoraciones: Valoraciones
)

data class Autor(
    val id: String,
    val nombre: String
)

data class Valoraciones(
    val cantidad: Int,
    val promedio: Float?,
    @SerializedName("total_comentarios") val totalComentarios: Int,
    @SerializedName("valoracion_maxima") val valoracionMaxima: Float?,
    @SerializedName("valoracion_minima") val valoracionMinima: Float?
)

data class RecentRecipesWrapper(
    @SerializedName("ultimas_recetas") val recetas: List<RecentRecipeReturn>
)

data class LoginRequest (
    val email: String,
    val password: String
)

data class RecipeTypeReturn (
    val id: String,
    val nombre: String
)

data class RecoverPasswordRequest (
    val email: String
)

data class RecoverPasswordReturn (
    val message: String
)

data class RecoverPasswordValidateRequest(
    val email: String,
    val codigo: String,
    val nuevaClave: String,
    val confirmacion: String
)

data class EmailRecovery(
    val email: String
)

data class ImagenRecetaResponse (
    @SerializedName("receta_id") val recetaId: String,
    @SerializedName("receta_titulo") val recetaTitulo: String,
    val imagenes: List<Imagen>,
    val total: Int
)


data class RecetaConImagen(
    val id: String,
    val nombre: String,
    val createdAt: String,
    val imagenUrl: String?
)

data class UsuarioResponse(
    val id: String,
    val nombre: String,
    val email: String,
    @SerializedName("fecha_registro") val fechaRegistro: String,
    @SerializedName("imagen_principal") val imagenPrincipal: String?, // Puede ser null
    @SerializedName("imagenes_perfil") val imagenesPerfil: ImagenesPerfil,
    val estadisticas: EstadisticasUsuario
)

data class ImagenesPerfil(
    val total: Int,
    val imagenes: List<Imagen>
)

data class EstadisticasUsuario(
    @SerializedName("tiene_foto_perfil") val tieneFotoPerfil: Boolean,
    @SerializedName("total_fotos") val totalFotos: Int,
    @SerializedName("cuenta_desde") val cuentaDesde: String
)

data class Imagen(
    val id: String,
    val url: String,
    @SerializedName("nombre_archivo") val nombreArchivo: String,
    val orden: Int,
    @SerializedName("es_principal") val esPrincipal: Boolean,
    @SerializedName("fecha_subida") val fechaSubida: String
)

data class AddToListRequest(
    @SerializedName("receta_id") val recetaId: String
)

data class AddToListResponse (
    val message: String
)

data class TiposReceta(
    val id: Int,
    val nombre: String
)

data class RecetaPost(
    val nombre: String,
    val tipo_id: Int,
    val descripcion: String,
    val dificultad: Dificultad,
    val ingredientes: List<IngredientePost>,
    val pasos: List<PasoPost>
)

data class Dificultad(val nombre: String)

data class IngredientePost(
    val nombre: String,
    val cantidad: Int,
    val unidad: String
)

data class PasoPost(
    val paso_numero: Int,
    val descripcion: String
)

data class CrearRecetaResponse(
    val id: String
)

// Clases de datos para la app - Modelos existentes
data class Comentario(
    val nombre: String,
    val texto: String,
    val tiempo: String
)

data class Receta(
    val nombre: String,
    val tipo_id: Int,
    val dificultad_id: Int,
    val descripcion: String,
    val ingredientes: List<Ingrediente>,
    val pasos: List<Paso>
)

data class Paso(
    val paso_numero: Int,
    val descripcion: String
)

data class Ingrediente(
    val nombre: String,
    val cantidad: Int,
    val unidad: String
)

// Nuevos modelos para la API
data class RecetaDetalle(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val fecha_creacion: String,
    val tipo: String,
    val tipo_id: Int,
    val dificultad: String,
    val dificultad_id: Int,
    val autor: String,
    val autor_id: String,
    val ingredientes: List<IngredienteDetalle>,
    val pasos: List<PasoDetalle>
)

data class IngredienteDetalle(
    val id: String,
    val nombre: String,
    val cantidad: Int,
    val unidad: String
)

data class PasoDetalle(
    val id: String,
    val paso_numero: Int,
    val descripcion: String
)

// Nuevos modelos para comentarios
data class ComentarioResponse(
    val receta: RecetaInfo,
    val estadisticas: EstadisticasComentarios,
    val distribucion_valoraciones: List<DistribucionValoracion>,
    val comentarios: List<ComentarioModel>
)

data class RecetaInfo(
    val id: String,
    val nombre: String
)

data class EstadisticasComentarios(
    val total_comentarios: Int,
    val total_valoraciones: Int,
    val promedio_valoracion: Double,
    val valoracion_minima: Int,
    val valoracion_maxima: Int
)

data class DistribucionValoracion(
    val estrellas: Int,
    val cantidad: Int
)

data class ComentarioModel(
    val id: String,
    val texto: String,
    val valoracion: Int,
    val fecha: String,
    val autor: String,
    val autor_id: String
)

// Modelo para enviar comentarios
data class ComentarioRequest(
    val recetaId: String,
    val texto: String,
    val valoracion: Int
)

data class ImagenDetalle(
    val id: String,
    val url: String,
    val nombre_archivo: String,
    val orden: Int,
    val es_principal: Boolean,
    val fecha_subida: String
)

data class video(
    val url_video: String,
    val nombre_archivo: String
)
