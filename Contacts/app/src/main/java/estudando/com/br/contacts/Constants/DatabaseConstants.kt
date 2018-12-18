package estudando.com.br.contacts.Constants

import android.provider.BaseColumns

object DatabaseConstants {

    const val CREATE_ENTRIES: String = """
       CREATE TABLE ${DatabaseConstants.User.TABLE_NAME} (
        ${BaseColumns._ID} iNTEGER PRIMARY KEY,
        ${DatabaseConstants.User.COLUMN_NAME} TEXT,
        ${DatabaseConstants.User.COLUMN_EMAIL} TEXT,
        ${DatabaseConstants.User.COLUMN_IMG} BLOB,
        ${DatabaseConstants.User.COLUMN_PHONE} TEXT
       );
    """

    const val DATABASE_VERSION: Int = 1

    object User : BaseColumns {
        const val TABLE_NAME: String = "Contacts"
        const val COLUMN_NAME: String = "name"
        const val COLUMN_EMAIL: String = "email"
        const val COLUMN_PHONE: String = "phone"
        const val COLUMN_IMG: String = "img"
    }
}