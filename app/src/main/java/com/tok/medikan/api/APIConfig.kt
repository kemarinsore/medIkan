package com.tok.medikan.api

import com.tok.medikan.data.disease.DiseaseResponse
import com.tok.medikan.data.history.HistoryResponse
import com.tok.medikan.data.user.UserResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface APIConfig {

    companion object {
        val BASE_URL = "https://web-service-dot-medikan.et.r.appspot.com/"

        fun build(token: String = ""): APIConfig {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                    chain.proceed(request.build())
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(APIConfig::class.java)
        }
    }

    @FormUrlEncoded
    @POST("users/register")
    fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("users/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @GET("users")
    fun userGet(): Call<UserResponse>

    @FormUrlEncoded
    @PUT("users/edit-account")
    fun updateProfile(
        @Field("username") username: String,
        @Field("email") email: String
    ): Call<UserResponse>

    @Multipart
    @PUT("users/edit-profile-picture")
    fun updatePicProfile(
        @Part file: MultipartBody.Part
    ): Call<UserResponse>

    @GET("users/history/{id}")
    fun history(
        @Path("id") id: String
    ): Call<HistoryResponse>

    @Multipart
    @POST("")
    fun disease(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @Part("historyName") name: String,
        @Part("userId") userId: Int,
    ): Call<DiseaseResponse>
}