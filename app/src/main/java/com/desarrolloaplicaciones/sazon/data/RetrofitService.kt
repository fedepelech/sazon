package com.desarrolloaplicaciones.sazon.data;
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST;
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Path


interface RetrofitService {
    @POST ("/api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginReturn;

    @GET ("/api/recetas/ultimas")
    suspend fun getRecentRecipes(): Response<RecentRecipesWrapper>;


    @GET("/api/recetas/ingredientes")
    suspend fun getRecipesWithIncludeFilter(
        @Query("incluye") ingredient: String)
    : Response<RecentRecipesWrapper>;

    @GET("/api/recetas/ingredientes")
    suspend fun getRecipesWithExcludeFilter(
        @Query("excluye") ingredient: String)
            : Response<RecentRecipesWrapper>;

    @GET("/api/tipos_receta")
    suspend fun getRecipeTypes(): List<RecipeTypeReturn>;

    @GET("/api/recetas/tipo")
    suspend fun getRecipesByType(
        @Query("tipo") type: String
    ): Response<RecentRecipesWrapper>;

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
    ): List<RecentRecipeReturn2>

    @GET("/api/lista")
    suspend fun getRecetasGuardadas(@Header("Authorization") token: String): Response<RecentRecipesWrapper>;

    @POST("/api/lista")
    suspend fun addToList(
        @Header("Authorization") token: String,
        @Body requestBody: AddToListRequest
    ): retrofit2.Response<AddToListResponse>

    @DELETE("/api/lista/{recetaId}")
    suspend fun removeFromList(
        @Header("Authorization") token: String,
        @Path("recetaId") recetaId: String
    ): retrofit2.Response<Unit>

    @GET("/api/recetas/{id}/imagenes")
    suspend fun obtenerImagenesReceta(@Path("id") recetaId: String): ImagenRecetaResponse

    @GET("/api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") usuarioId: String): UsuarioResponse

    @GET("/api/tipos_receta")
    suspend fun obtenerCategorias(): List<TiposReceta>

    @GET("api/recetas/{recetaId}")
    suspend fun obtenerRecetaPorId(@Path("recetaId") recetaId: String): RecetaDetalle

    @GET("api/comentarios/receta/{recetaId}")
    suspend fun obtenerComentariosPorReceta(@Path("recetaId") recetaId: String): ComentarioResponse

    @POST("api/comentarios")
    suspend fun crearComentario(
        @Header("Authorization") token: String,
        @Body comentario: ComentarioRequest
    ): ComentarioModel

    @POST("/api/recetas")
    suspend fun subirReceta(
        @Header("Authorization") token: String,
        @Body receta: RecetaPost
    ): Response<CrearRecetaResponse>


    @GET("/api/ingredientes")
    suspend fun obtenerIngredientes(): List<Ingrediente>

    @Multipart
    @POST("/api/recetas/{recetaId}/imagenes/upload")
    suspend fun subirImagenReceta(@Path("recetaId") recetaId: String,
                                  @Part imagen: MultipartBody.Part,
                                  @Header("Authorization") token: String,): Response<Unit>




    @POST("/api/recetas/{recetaId}/videos/upload")
    suspend fun subirVideoReceta(
        @Path("recetaId") recetaId: String,
        @Header("Authorization") token: String,
        @Body video: video,
    ): Response<CrearRecetaResponse>
}

object RetrofitServiceFactory {
    fun makeRetrofitService(): RetrofitService {
        return Retrofit.Builder()
            .baseUrl("https://recetasapp-blue.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService::class.java)
    }

}
