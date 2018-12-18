package estudando.com.br.contacts.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.lang.Exception

fun Bitmap.bitmapToBlob(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}

fun ByteArray.byteToBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)

class Utils {
    companion object {
        fun getImageUri(bitmap: Bitmap, context: Context): Uri {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Contacts", null)
            return Uri.parse(path)
        }

        fun getRealPathFromUri(context: Context, uri: Uri): String {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val value = cursor.getString(index)
            cursor.close()
            return value
        }

        fun handlerOrientation(bitmap: Bitmap, path: String): Bitmap {
            val ei = ExifInterface(path)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            return when(orientation) {
                90 -> rotateImage(bitmap, 90.toFloat())
                180 -> rotateImage(bitmap, 180.toFloat())
                270 -> rotateImage(bitmap, 270.toFloat())
                else -> rotateImage(bitmap, 0.toFloat())
            }
        }

        fun rotateImage(bitmap: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(bitmap,0, 0, bitmap.width, bitmap.height, matrix, true)
        }

    }
}
