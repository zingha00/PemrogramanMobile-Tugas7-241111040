package com.utama.aplikasiloginsederhana

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EventDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "event_app.db"
        private const val DB_VERSION = 1
        const val TABLE_EVENTS = "events"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_DATE = "date"
        const val COL_LOCATION = "location"
        const val COL_PRICE = "price"
        const val COL_DESCRIPTION = "description"
        const val COL_REGISTERED = "is_registered"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_EVENTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT NOT NULL,
                $COL_DATE TEXT NOT NULL,
                $COL_LOCATION TEXT NOT NULL,
                $COL_PRICE INTEGER NOT NULL DEFAULT 0,
                $COL_DESCRIPTION TEXT,
                $COL_REGISTERED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTable)
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        val sampleEvents = listOf(
            arrayOf("Seminar AI & Masa Depan", "15 Mei 2026", "Hotel Grand Slipi", "50000", "Seminar tentang perkembangan AI di Indonesia", "0"),
            arrayOf("Workshop Kotlin Android", "20 Mei 2026", "Kampus Teknik", "75000", "Workshop intensif pengembangan Android", "0"),
            arrayOf("Web Developer Gathering", "25 Mei 2026", "Co-working Space", "0", "Networking para developer web", "0"),
            arrayOf("UI/UX Design Bootcamp", "1 Juni 2026", "Online (Zoom)", "100000", "Bootcamp desain UI/UX profesional", "1"),
            arrayOf("Tech Career Fair 2026", "10 Juni 2026", "Convention Center", "0", "Pameran karir di bidang teknologi", "0")
        )
        sampleEvents.forEach { e ->
            val cv = ContentValues().apply {
                put(COL_NAME, e[0])
                put(COL_DATE, e[1])
                put(COL_LOCATION, e[2])
                put(COL_PRICE, e[3].toInt())
                put(COL_DESCRIPTION, e[4])
                put(COL_REGISTERED, e[5].toInt())
            }
            db.insert(TABLE_EVENTS, null, cv)
        }
    }
}