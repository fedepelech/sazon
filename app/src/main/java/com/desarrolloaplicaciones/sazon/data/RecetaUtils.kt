package com.desarrolloaplicaciones.sazon.data

suspend fun completarImagenesRecetas2(
    retrofitService: RetrofitService,
    recetas: List<RecentRecipeReturn2>
): List<RecetaConImagen> {
    return recetas.map { receta ->
        val imagenUrl = try {
            val imagenesResponse = retrofitService.obtenerImagenesReceta(receta.id)
            imagenesResponse.imagenes
                .getOrNull(0)
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

//TODO: QUitar wrapper y usar solo las recetas de la lista
suspend fun completarImagenesRecetas(
    retrofitService: RetrofitService,
    recetasWrapper: RecentRecipesWrapper
): List<RecetaConImagen> {
    return recetasWrapper.recetas.map { receta ->
        val imagenUrl = try {
            val imagenesResponse = retrofitService.obtenerImagenesReceta(receta.id)
            imagenesResponse.imagenes
                .getOrNull(0)
                ?.url
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

suspend fun completarImagenesRecetas3(
    retrofitService: RetrofitService,
    recetasWrapper: RecentRecipesWrapperSaved
): List<RecetaConImagen> {
    return recetasWrapper.recetas.map { receta ->
        val imagenUrl = try {
            val imagenesResponse = retrofitService.obtenerImagenesReceta(receta.id)
            imagenesResponse.imagenes
                .getOrNull(0)
                ?.url
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
