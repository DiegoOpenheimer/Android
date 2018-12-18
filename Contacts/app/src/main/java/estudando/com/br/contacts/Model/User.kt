package estudando.com.br.contacts.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val name: String? = null, val email: String? = null, val phone: String? = null, val img: ByteArray? = null, var id: Long? = null) : Parcelable {

    override fun toString() = "User($name, $email, $phone, ${img.toString()}, $id)"

}