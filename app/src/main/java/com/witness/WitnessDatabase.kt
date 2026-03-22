package com.witness

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class WitnessDatabase(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val TAG = "WitnessDB"
        private const val DATABASE_NAME = "witness.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_PAUSES = "pause_log"
        private const val TABLE_SESSIONS = "sessions"
        private const val COL_ID = "id"
        private const val COL_APP_PACKAGE = "app_package"
        private const val COL_PLATFORM = "platform"
        private const val COL_PAUSE_DURATION_MS = "pause_duration_ms"
        private const val COL_TIMESTAMP = "timestamp"
        private const val COL_SESSION_ID = "session_id"
        private const val COL_SESSION_START = "session_start"
        private const val COL_SESSION_END = "session_end"
        private const val COL_PAUSE_COUNT = "pause_count"
        private const val COL_TOTAL_SCROLL_TIME_MS = "total_scroll_time_ms"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_PAUSES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_APP_PACKAGE TEXT NOT NULL,
                $COL_PLATFORM TEXT NOT NULL,
                $COL_PAUSE_DURATION_MS INTEGER NOT NULL,
                $COL_TIMESTAMP INTEGER NOT NULL,
                $COL_SESSION_ID TEXT NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE $TABLE_SESSIONS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SESSION_ID TEXT UNIQUE NOT NULL,
                $COL_APP_PACKAGE TEXT NOT NULL,
                $COL_PLATFORM TEXT NOT NULL,
                $COL_SESSION_START INTEGER NOT NULL,
                $COL_SESSION_END INTEGER,
                $COL_PAUSE_COUNT INTEGER DEFAULT 0,
                $COL_TOTAL_SCROLL_TIME_MS INTEGER DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX idx_pause_timestamp ON $TABLE_PAUSES($COL_TIMESTAMP)")
        db.execSQL("CREATE INDEX idx_pause_session ON $TABLE_PAUSES($COL_SESSION_ID)")
        db.execSQL("CREATE INDEX idx_pause_platform ON $TABLE_PAUSES($COL_PLATFORM)")
        Log.i(TAG, "Database created")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PAUSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SESSIONS")
        onCreate(db)
    }

    fun insertPause(
        appPackage: String, platform: String, pauseDurationMs: Long,
        timestamp: Long, sessionId: String
    ): Long {
        val values = ContentValues().apply {
            put(COL_APP_PACKAGE, appPackage)
            put(COL_PLATFORM, platform)
            put(COL_PAUSE_DURATION_MS, pauseDurationMs)
            put(COL_TIMESTAMP, timestamp)
            put(COL_SESSION_ID, sessionId)
        }
        val rowId = writableDatabase.insert(TABLE_PAUSES, null, values)
        if (rowId == -1L) Log.e(TAG, "Failed to insert pause row")
        updateSession(appPackage, platform, sessionId, timestamp, pauseDurationMs)
        return rowId
    }

    private fun updateSession(
        appPackage: String, platform: String, sessionId: String,
        timestamp: Long, pauseDurationMs: Long
    ) {
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_ID, $COL_PAUSE_COUNT FROM $TABLE_SESSIONS WHERE $COL_SESSION_ID = ?",
            arrayOf(sessionId)
        )
        if (cursor.moveToFirst()) {
            val currentCount = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAUSE_COUNT))
            db.execSQL("""
                UPDATE $TABLE_SESSIONS
                SET $COL_PAUSE_COUNT = ${currentCount + 1}, $COL_SESSION_END = $timestamp
                WHERE $COL_SESSION_ID = '$sessionId'
            """.trimIndent())
        } else {
            val values = ContentValues().apply {
                put(COL_SESSION_ID, sessionId)
                put(COL_APP_PACKAGE, appPackage)
                put(COL_PLATFORM, platform)
                put(COL_SESSION_START, timestamp)
                put(COL_SESSION_END, timestamp)
                put(COL_PAUSE_COUNT, 1)
                put(COL_TOTAL_SCROLL_TIME_MS, pauseDurationMs)
            }
            db.insert(TABLE_SESSIONS, null, values)
        }
        cursor.close()
    }

    fun totalPauseCount(): Int {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_PAUSES", null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    data class PauseRecord(
        val id: Long, val appPackage: String, val platform: String,
        val pauseDurationMs: Long, val timestamp: Long, val sessionId: String
    )
}
