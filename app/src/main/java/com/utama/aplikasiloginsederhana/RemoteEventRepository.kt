package com.utama.aplikasiloginsederhana

import android.util.Log

class RemoteEventRepository {
    private val api = RetrofitClient.apiService

    suspend fun getAllEvents(): List<Event> {
        val response = api.getAllEvents()
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.success == true) {
                return body.data?.map { it.toEvent() } ?: emptyList()
            }
            throw Exception(body?.message ?: "Gagal mengambil data event")
        }
        throw Exception("HTTP Error: ${response.code()}")
    }

    suspend fun addEvent(event: Event): Int {
        val request = EventRequest(
            name = event.name,
            date = event.date,
            location = event.location,
            price = event.price,
            description = event.description
        )
        
        Log.d("API_DEBUG", "Mengirim data: $request")
        
        val response = api.addEvent(request)
        if (response.isSuccessful) {
            val body = response.body()
            Log.d("API_DEBUG", "Response Sukses: ${body?.message}")
            if (body?.success == true) {
                // PHP Anda harus mengembalikan ID di dalam data: {"id": 10}
                return body.data?.get("id") ?: 0
            }
            throw Exception(body?.message ?: "Server menolak data (success=false)")
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("API_DEBUG", "Response Gagal (${response.code()}): $errorBody")
            throw Exception("Server Error: ${response.code()}")
        }
    }

    suspend fun deleteEvent(id: Int): Boolean {
        val response = api.deleteEvent(id)
        return response.isSuccessful && response.body()?.success == true
    }

    suspend fun updateEvent(event: Event): Boolean {
        val request = EventRequest(event.name, event.date, event.location, event.price, event.description)
        val response = api.updateEvent(event.id, request)
        return response.isSuccessful && response.body()?.success == true
    }

    suspend fun registerEvent(userId: Int, eventId: Int): Boolean {
        val body = mapOf("user_id" to userId, "event_id" to eventId)
        val response = api.registerEvent(body)
        if (response.isSuccessful) {
            return response.body()?.success == true
        }
        throw Exception("Gagal registrasi: ${response.message()}")
    }
}