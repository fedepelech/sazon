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