package com.example.expensetrackingpro

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.expensetrackingpro.UserContract

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(UserContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(UserContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun insertUser(username: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_USERNAME, username)
            put(UserContract.UserEntry.COLUMN_EMAIL, email)
            put(UserContract.UserEntry.COLUMN_PASSWORD, password)
        }

        return try {
            val newRowId = db.insertOrThrow(UserContract.UserEntry.TABLE_NAME, null, values)
            db.close()
            newRowId
        } catch (e: SQLiteConstraintException) {
            // Handle constraint violation error
            -1
        }
    }


    fun getUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(
            UserContract.UserEntry.COLUMN_USERNAME,
            UserContract.UserEntry.COLUMN_PASSWORD
        )
        val selection =
            "${UserContract.UserEntry.COLUMN_USERNAME} = ? AND ${UserContract.UserEntry.COLUMN_PASSWORD} = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor: Cursor? = db.query(
            UserContract.UserEntry.TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val userExists = cursor?.count ?: 0 > 0
        cursor?.close()
        return userExists
    }
    fun updateUserPassword(username: String, newPassword: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_PASSWORD, newPassword)
        }

        db.update(UserContract.UserEntry.TABLE_NAME, contentValues, "${UserContract.UserEntry.COLUMN_USERNAME} = ?", arrayOf(username))
        db.close()
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MyApp.db"
    }
}