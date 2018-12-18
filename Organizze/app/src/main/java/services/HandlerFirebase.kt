package services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HandlerFirebase {

    companion object {
        private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        private val databaseFirebase: DatabaseReference = FirebaseDatabase.getInstance().getReference()

        fun getFirebaseAuth(): FirebaseAuth = firebaseAuth
        fun getFirebaseDatabase(): DatabaseReference = databaseFirebase
    }

}