package com.utama.aplikasiloginsederhana

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Emulator Android Studio pakai 10.0.2.2
    // HP fisik pakai IP WiFi kamu misal 192.168.1.x
    private const val BASE_URL = "http://10.0.2.2/event_api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}