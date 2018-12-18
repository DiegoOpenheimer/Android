package services

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import helper.DateCustom
import helper.stringToBase64
import model.Movement
import model.User
import java.lang.Exception

class HandlerUserDatabaseFirebase {

    val databaseReference: DatabaseReference = HandlerFirebase.getFirebaseDatabase()

    fun saveUser(user: User) {
        databaseReference.child("users")
            .child(user.email.stringToBase64())
            .setValue(user)
    }

    fun saveMovement(movement: Movement, callback: () -> Unit, callBackError: (e: Exception) -> Unit) {

        val user: FirebaseUser? = HandlerFirebase.getFirebaseAuth().currentUser

        databaseReference.child("Movement")
            .child(user?.email.toString().stringToBase64())
            .child(DateCustom.getOnlyMonthAndYear(movement.date.toString()))
            .push()
            .setValue(movement)
            .addOnCompleteListener {
              callback()
            }
            .addOnFailureListener{
               callBackError(it)
            }
    }

}