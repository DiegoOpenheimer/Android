package helper

import android.util.Base64

fun String.stringToBase64(): String = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT).replace("\\n|\\r".toRegex(), "")
fun String.base64ToString(): String = Base64.decode(this, Base64.DEFAULT).toString()
