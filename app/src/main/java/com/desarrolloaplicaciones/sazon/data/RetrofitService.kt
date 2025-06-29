package com.desarrolloaplicaciones.sazon.data;
import retrofit2.Response
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET
import retrofit2.http.POST;
import retrofit2.http.Query
import retrofit2.http.Path


interface RetrofitService {
    @POST ("/api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginReturn;

    @GET ("/api/recetas/ultimas")
    suspend fun getRecentRecipes(): List<RecentRecipeReturn>;


    @GET("/api/recetas/ingredientes")
    suspend fun getRecipesWithIncludeFilter(
        @Query("incluye") ingredient: String)
    : List<RecentRecipeReturn>;

    @GET("/api/recetas/ingredientes")
    suspend fun getRecipesWithExcludeFilter(
        @Query("excluye") ingredient: String)
            : List<RecentRecipeReturn>;

    @GET("/api/tipos_receta")
    suspend fun getRecipeTypes(): List<RecipeTypeReturn>;

    @GET("/api/recetas/tipo")
    suspend fun getRecipesByType(
        @Query("tipo") type: String
    ): List<RecentRecipeReturn>;

    @POST("/api/recuperar-clave")
    suspend fun recoverPassword(
        @Body body: RecoverPasswordRequest
    ): retrofit2.Response<RecoverPasswordReturn>

    @POST("/api/recuperar-clave/validar")
    suspend fun recoverPasswordValidate(
        @Body body: RecoverPasswordValidateRequest
    ): retrofit2.Response<RecoverPasswordReturn>

    @GET("/api/recetas/usuario/{usuarioId}")
    suspend fun getRecetasPorUsuario(
        @Path("usuarioId") usuarioId: String
    ): List<RecentRecipeReturn>

    @GET ("/api/lista")
    suspend fun getRecetasGuardadas(): List<RecentRecipeReturn>;

    @GET("/api/recetas/{id}/imagenes")
    suspend fun obtenerImagenesReceta(@Path("id") recetaId: String): ImagenRecetaResponse

    @GET("/api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") usuarioId: String): UsuarioResponse

    @GET("/api/tipos_receta")
    suspend fun obtenerCategorias(): List<TiposReceta>

    @POST("/api/recetas")
    suspend fun subirReceta(
            @Body receta: RecetaPost
    ): Response<Unit>
}

object RetrofitServiceFactory {
    fun makeRetrofitService(): RetrofitService {
        return Retrofit.Builder()
            .baseUrl("https://recetasapp-blue.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService::class.java)
    }

}
