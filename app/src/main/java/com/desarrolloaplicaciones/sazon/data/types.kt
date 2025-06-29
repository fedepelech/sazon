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

data class RecentRecipeReturn (
    val id: String,
    val nombre: String,
    @SerializedName("created_at") val createdAt: String
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

data class ImagenRecetaResponse(
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



