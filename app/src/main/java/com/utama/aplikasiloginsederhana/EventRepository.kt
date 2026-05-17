package com.utama.aplikasiloginsederhana

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.utama.aplikasiloginsederhana.EventDatabaseHelper as DBH

class EventRepository(context: Context) {
    private val db = DBH(context)

    fun addEvent(event: Event): Long {
        val cv = ContentValues().apply {
            put(DBH.COL_NAME, event.name)
            put(DBH.COL_DATE, event.date)
            put(DBH.COL_LOCATION, event.location)
            put(DBH.COL_PRICE, event.price)
            put(DBH.COL_REGISTERED, 0)
        }
        return db.writableDatabase.insert(DBH.TABLE_EVENTS, null, cv)
    }

    fun getAllEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val cursor: Cursor = db.readableDatabase.query(
            DBH.TABLE_EVENTS, null, null, null, null, null,
            "${DBH.COL_DATE} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                events.add(
                    Event(
                        id = it.getInt(it.getColumnIndexOrThrow(DBH.COL_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(DBH.COL_NAME)),
                        date = it.getString(it.getColumnIndexOrThrow(DBH.COL_DATE)),
                        location = it.getString(it.getColumnIndexOrThrow(DBH.COL_LOCATION)),
                        price = it.getInt(it.getColumnIndexOrThrow(DBH.COL_PRICE))
                    )
                )
            }
        }
        return events
    }

    fun getEventById(id: Int): Event? {
        val cursor = db.readableDatabase.query(
            DBH.TABLE_EVENTS, null,
            "${DBH.COL_ID} = ?", arrayOf(id.toString()),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) Event(
                id = it.getInt(it.getColumnIndexOrThrow(DBH.COL_ID)),
                name = it.getString(it.getColumnIndexOrThrow(DBH.COL_NAME)),
                date = it.getString(it.getColumnIndexOrThrow(DBH.COL_DATE)),
                location = it.getString(it.getColumnIndexOrThrow(DBH.COL_LOCATION)),
                price = it.getInt(it.getColumnIndexOrThrow(DBH.COL_PRICE))
            ) else null
        }
    }

    fun updateEvent(event: Event): Int {
        val cv = ContentValues().apply {
            put(DBH.COL_NAME, event.name)
            put(DBH.COL_DATE, event.date)
            put(DBH.COL_LOCATION, event.location)
            put(DBH.COL_PRICE, event.price)
        }
        return db.writableDatabase.update(
            DBH.TABLE_EVENTS, cv,
            "${DBH.COL_ID} = ?", arrayOf(event.id.toString())
        )
    }

    fun deleteEvent(id: Int): Int {
        return db.writableDatabase.delete(
            DBH.TABLE_EVENTS,
            "${DBH.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun setRegistered(id: Int, isRegistered: Boolean): Int {
        val cv = ContentValues().apply {
            put(DBH.COL_REGISTERED, if (isRegistered) 1 else 0)
        }
        return db.writableDatabase.update(
            DBH.TABLE_EVENTS, cv,
            "${DBH.COL_ID} = ?", arrayOf(id.toString())
        )
    }

    fun countRegisteredEvents(): Int {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM ${DBH.TABLE_EVENTS} WHERE ${DBH.COL_REGISTERED} = 1",
            null
        )
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }
}