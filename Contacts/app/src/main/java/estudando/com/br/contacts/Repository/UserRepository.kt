package estudando.com.br.contacts.Repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import estudando.com.br.contacts.Constants.DatabaseConstants
import estudando.com.br.contacts.Model.User
import java.lang.Exception

class UserRepository(private val context: Context) {

    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)

    fun saveUser(user: User) {
        try {
            val db = databaseHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseConstants.User.COLUMN_NAME, user.name)
                put(DatabaseConstants.User.COLUMN_PHONE, user.phone)
                put(DatabaseConstants.User.COLUMN_EMAIL, user.email)
                put(DatabaseConstants.User.COLUMN_IMG, user.img)
            }
            db.insert(DatabaseConstants.User.TABLE_NAME, null, values)
        } catch (e: Exception) {
            throw e
        }
    }

    fun updateUser(user: User) {
        try {
            val selection: String = "${BaseColumns._ID} = ?"
            val selectionArgs = arrayOf(user.id.toString())
            val values = ContentValues().apply {
                put(DatabaseConstants.User.COLUMN_NAME, user.name)
                put(DatabaseConstants.User.COLUMN_PHONE, user.phone)
                put(DatabaseConstants.User.COLUMN_EMAIL, user.email)
                put(DatabaseConstants.User.COLUMN_IMG, user.img)
            }
            val db = databaseHelper.writableDatabase
            db.update(DatabaseConstants.User.TABLE_NAME, values, selection, selectionArgs)
        } catch(e: Exception) {
            throw e
        }
    }

    fun deleteUser(user: User) {
        try {
            val selection: String = "${BaseColumns._ID} LIKE ?"
            val selectionArgs: Array<String> = arrayOf(user.id.toString())
            val db = databaseHelper.writableDatabase
            db.delete(DatabaseConstants.User.TABLE_NAME, selection, selectionArgs)
        } catch(e: Exception) {
            throw e
        }
    }

    fun getAll(): MutableList<User> {
        try {
            val projection: Array<String> = arrayOf(
                BaseColumns._ID,
                DatabaseConstants.User.COLUMN_NAME,
                DatabaseConstants.User.COLUMN_EMAIL,
                DatabaseConstants.User.COLUMN_PHONE,
                DatabaseConstants.User.COLUMN_IMG
                )

            val db = databaseHelper.readableDatabase
            val cursor: Cursor = db.query(
                DatabaseConstants.User.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
                )
            return mountUser(cursor)
        } catch(e: Exception) {
            throw e
        }
    }

    fun mountUser(cursor: Cursor): MutableList<User> {
        val users: MutableList<User> = mutableListOf()
        try {
            if (cursor != null && cursor.count > 0 && cursor.moveToFirst()) {
                do {
                users.add(
                        User(
                            name = cursor.getString(cursor.getColumnIndex(DatabaseConstants.User.COLUMN_NAME)),
                            email = cursor.getString(cursor.getColumnIndex(DatabaseConstants.User.COLUMN_EMAIL)),
                            phone = cursor.getString(cursor.getColumnIndex(DatabaseConstants.User.COLUMN_PHONE)),
                            img = cursor.getBlob(cursor.getColumnIndex(DatabaseConstants.User.COLUMN_IMG)),
                            id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                        )
                )
                } while(cursor.moveToNext())
            }
            cursor.close()
            return users
        } catch (e: Exception) {
            return users
        }
    }

}