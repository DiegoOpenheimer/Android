package estudando.com.br.contacts.Repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import estudando.com.br.contacts.Constants.DatabaseConstants


class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, tableName, null, DatabaseConstants.DATABASE_VERSION) {


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DatabaseConstants.CREATE_ENTRIES)
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DatabaseConstants.CREATE_ENTRIES)
    }

    companion object {
        private val tableName: String = "Contacts.db"
        private lateinit var INSTANCE: DatabaseHelper

        fun getInstance(context: Context): DatabaseHelper {
            if (INSTANCE == null) {
                INSTANCE = DatabaseHelper(context)
            }

            return INSTANCE
        }
    }
}