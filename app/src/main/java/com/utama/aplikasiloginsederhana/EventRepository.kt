package com.utama.aplikasiloginsederhana

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.utama.aplikasiloginsederhana.EventDatabaseHelper as DBH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository(private val context: Context) {
    private val localDb = DBH(context)
    private val remoteRepo = RemoteEventRepository()

    // ── READ: Ambil dari API lalu simpan ke Lokal ────────────────
    suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) {
        try {
            val remoteEvents = remoteRepo.getAllEvents()
            syncLocalDatabase(remoteEvents)
            remoteEvents
        } catch (e: Exception) {
            e.printStackTrace()
            getLocalEvents()
        }
    }

    // ── Sinkronisasi ke SQLite ───────────────────────────────────
    private fun syncLocalDatabase(events: List<Event>) {
        val db = localDb.writableDatabase
        db.delete(DBH.TABLE_EVENTS, null, null)
        events.forEach { event ->
            val cv = ContentValues().apply {
                put(DBH.COL_ID, event.id)
                put(DBH.COL_NAME, event.name)
                put(DBH.COL_DATE, event.date)
                put(DBH.COL_LOCATION, event.location)
                put(DBH.COL_PRICE, event.price)
                put(DBH.COL_DESCRIPTION, event.description)
                put(DBH.COL_REGISTERED, if (event.isRegistered) 1 else 0)
            }
            db.insertWithOnConflict(
                DBH.TABLE_EVENTS, null, cv,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
    }

    // ── READ: Hanya dari SQLite lokal ────────────────────────────
    private fun getLocalEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val cursor = localDb.readableDatabase.query(
            DBH.TABLE_EVENTS, null, null,
            null, null, null,
            "${DBH.COL_DATE} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                events.add(Event(
                    id = it.getInt(it.getColumnIndexOrThrow(DBH.COL_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(DBH.COL_NAME)),
                    date = it.getString(it.getColumnIndexOrThrow(DBH.COL_DATE)),
                    location = it.getString(it.getColumnIndexOrThrow(DBH.COL_LOCATION)),
                    price = it.getInt(it.getColumnIndexOrThrow(DBH.COL_PRICE)),
                    description = it.getString(it.getColumnIndexOrThrow(DBH.COL_DESCRIPTION)) ?: "",
                    isRegistered = it.getInt(it.getColumnIndexOrThrow(DBH.COL_REGISTERED)) == 1
                ))
            }
        }
        return events
    }

    // ── CREATE: Simpan ke MySQL lalu sinkron ke Lokal ────────────
    suspend fun addEvent(event: Event) = withContext(Dispatchers.IO) {
        // 1. Simpan ke MySQL (Jika gagal, throw Exception ke ViewModel)
        val remoteId = remoteRepo.addEvent(event)
        
        // 2. Simpan ke SQLite hanya jika MySQL sukses
        val cv = ContentValues().apply {
            put(DBH.COL_ID, remoteId)
            put(DBH.COL_NAME, event.name)
            put(DBH.COL_DATE, event.date)
            put(DBH.COL_LOCATION, event.location)
            put(DBH.COL_PRICE, event.price)
            put(DBH.COL_DESCRIPTION, event.description)
        }
        localDb.writableDatabase.insertWithOnConflict(
            DBH.TABLE_EVENTS, null, cv,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    // ── DELETE: Hapus di MySQL lalu di Lokal ─────────────────────
    suspend fun deleteEvent(id: Int) = withContext(Dispatchers.IO) {
        remoteRepo.deleteEvent(id)
        localDb.writableDatabase.delete(
            DBH.TABLE_EVENTS,
            "${DBH.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }

    // ── REGISTER: Daftar via API lalu update Lokal ───────────────
    suspend fun setRegistered(id: Int, isRegistered: Boolean) =
        withContext(Dispatchers.IO) {
            if (isRegistered) {
                val sessionManager = SessionManager(context)
                val userId = sessionManager.getUserId()
                if (userId > 0) {
                    remoteRepo.registerEvent(userId, id)
                } else {
                    throw Exception("Silakan login terlebih dahulu")
                }
            }

            val cv = ContentValues().apply {
                put(DBH.COL_REGISTERED, if (isRegistered) 1 else 0)
            }
            localDb.writableDatabase.update(
                DBH.TABLE_EVENTS, cv,
                "${DBH.COL_ID} = ?", arrayOf(id.toString())
            )
        }

    fun countRegisteredEvents(): Int {
        val cursor = localDb.readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM ${DBH.TABLE_EVENTS} WHERE ${DBH.COL_REGISTERED} = 1",
            null
        )
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }
}
