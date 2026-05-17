package com.utama.aplikasiloginsederhana

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.utama.aplikasiloginsederhana.EventDatabaseHelper as DBH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository(context: Context) {
    private val localDb = DBH(context)
    private val remoteRepo = RemoteEventRepository()

    // ── READ: Offline-first ──────────────────────────────────────
    suspend fun getAllEvents(): List<Event> = withContext(Dispatchers.IO) {
        try {
            val remoteEvents = remoteRepo.getAllEvents()
            syncLocalDatabase(remoteEvents)
            remoteEvents
        } catch (e: Exception) {
            getLocalEvents()
        }
    }

    // ── Sync API ke SQLite lokal ─────────────────────────────────
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
                    price = it.getInt(it.getColumnIndexOrThrow(DBH.COL_PRICE))
                ))
            }
        }
        return events
    }

    // ── CREATE ───────────────────────────────────────────────────
    suspend fun addEvent(event: Event): Long = withContext(Dispatchers.IO) {
        try { remoteRepo.addEvent(event) } catch (_: Exception) {}
        val cv = ContentValues().apply {
            put(DBH.COL_NAME, event.name)
            put(DBH.COL_DATE, event.date)
            put(DBH.COL_LOCATION, event.location)
            put(DBH.COL_PRICE, event.price)
        }
        localDb.writableDatabase.insert(DBH.TABLE_EVENTS, null, cv)
    }

    // ── DELETE ───────────────────────────────────────────────────
    suspend fun deleteEvent(id: Int): Int = withContext(Dispatchers.IO) {
        try { remoteRepo.deleteEvent(id) } catch (_: Exception) {}
        localDb.writableDatabase.delete(
            DBH.TABLE_EVENTS,
            "${DBH.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }

    // ── SET REGISTERED ───────────────────────────────────────────
    suspend fun setRegistered(id: Int, isRegistered: Boolean): Int =
        withContext(Dispatchers.IO) {
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