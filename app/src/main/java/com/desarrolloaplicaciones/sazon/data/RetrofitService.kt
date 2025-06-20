package com.desarrolloaplicaciones.sazon.data;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET
import retrofit2.http.POST;

interface RetrofitService {
    @POST ("/api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginReturn;

    @GET ("/api/recetas/ultimas")
    suspend fun getRecentRecipes(): List<RecentRecipeReturn>;
}
object RetrofitServiceFactory {
    fun makeRetrofitService(): RetrofitService {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.62:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService::class.java)
    }
}
