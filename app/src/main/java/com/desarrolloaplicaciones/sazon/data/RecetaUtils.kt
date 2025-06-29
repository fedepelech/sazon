package com.desarrolloaplicaciones.sazon.data

suspend fun completarImagenesRecetas(
    retrofitService: RetrofitService,
    recetas: List<RecentRecipeReturn>
): List<RecetaConImagen> {
    return recetas.map { receta ->
        val imagenUrl = try {
            val imagenesResponse = retrofitService.obtenerImagenesReceta(receta.id)
            imagenesResponse.imagenes
                .getOrNull(1) // Segunda imagen (índice 1)
                ?.url
                ?.let { it }
        } catch (e: Exception) {
            null
        }

        RecetaConImagen(
            id = receta.id,
            nombre = receta.nombre,
            createdAt = receta.createdAt,
            imagenUrl = imagenUrl
        )
    }
}