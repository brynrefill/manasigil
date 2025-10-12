package com.brynrefill.manasigil.ui.components

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

// API interface and data class
/**
 * singleton to manage Retrofit instance
 */
object ApiClient {

    // endpoint Solidals API (my external API) to generate strong passwords
    private const val BASE_URL = "https://brynrefill.pythonanywhere.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val passwordApi: PasswordGeneratorApi by lazy {
        retrofit.create(PasswordGeneratorApi::class.java)
    }
}

/**
 * Retrofit API interface
 */
interface PasswordGeneratorApi {
    @GET("gen-password")
    fun generatePassword(@Query("l") length: Int): Call<PasswordGeneratorResponse>
}

/**
 * response from password generation API
 */
data class PasswordGeneratorResponse(
    val password: String
)
