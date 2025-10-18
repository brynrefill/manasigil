package com.brynrefill.manasigil.data.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// API interface and data class
/**
 * singleton to manage Retrofit instance
 */
object ApiClient {

    // endpoint Solidals API (my external API) to generate strong passwords
    private const val API_BASE_URL = "https://brynrefill.pythonanywhere.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val solidalsApi: SolidalsApi by lazy {
        retrofit.create(SolidalsApi::class.java)
    }
}

/**
 * Retrofit API interface
 */
interface SolidalsApi {
    @GET("gen-password")
    fun generatePassword(@Query("l") length: Int): Call<PasswordGeneratorResponse>

    // this endpoint uses the Pwned Passwords API (service provided by HIBP)
    // @POST("check-breach")
    // fun checkPassword(@Query("p") password: String): Call<PasswordBreachResponse> // not sending the password in the request body
    @POST("check-breach")
    @FormUrlEncoded
    fun checkPassword(@Field("p") password: String): Call<PasswordBreachResponse>

    // if the API expects JSON body instead
    // @POST("check-breach")
    // fun checkPassword(@Body request: CheckPasswordRequest): Call<PasswordBreachResponse>
}

/**
 * response from password generation API endpoint
 */
data class PasswordGeneratorResponse(
    val password: String
)

/**
 * response from password breach check API endpoint
 */
data class PasswordBreachResponse(
    val breached: Boolean,
    val count: Int
)
