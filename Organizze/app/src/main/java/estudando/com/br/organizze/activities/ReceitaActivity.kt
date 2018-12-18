package estudando.com.br.organizze.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import estudando.com.br.organizze.R
import helper.DateCustom
import helper.stringToBase64
import kotlinx.android.synthetic.main.activity_receita.*
import model.Movement
import model.User
import services.HandlerFirebase
import services.HandlerUserDatabaseFirebase

class ReceitaActivity : AppCompatActivity() {

    private val handlerUserDatabaseFirebase: HandlerUserDatabaseFirebase = HandlerUserDatabaseFirebase()
    private lateinit var userReference: DatabaseReference
    private var totalIncoming: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receita)
        fab.setOnClickListener(::validateFields)
        inputDate.setText(DateCustom.getCurrentDateFormatted())
        getReferenceOfTheUser()
    }

    private fun validateFields(v: View) {
        val value: String = valueIncoming.text.toString()
        val date: String = inputDate.text.toString()
        val category: String = inputCategory.text.toString()
        val description: String = inputDescription.text.toString()
        if ( value.isNullOrBlank() || date.isNullOrBlank() || category.isNullOrBlank() || description.isNullOrBlank() ) {
            Toast.makeText(this, "Preencha todos os valores", Toast.LENGTH_LONG).show()
        } else {
            saveIncoming(value, date, category, description)
        }
    }

    private fun saveIncoming(value: String, date: String, category: String, description: String) {
        progressBarIncoming.visibility = View.VISIBLE
        val movement: Movement = Movement()
        movement.category = category
        movement.description = description
        movement.date = date
        movement.value = value.toDouble()
        movement.type = "r"
        handlerUserDatabaseFirebase.saveMovement(movement, ::callbackSuccess, ::callbackError)
        updateIncoming( value.toDouble() )
    }

    private fun callbackSuccess() {
        progressBarIncoming.visibility = View.INVISIBLE
        Toast.makeText(this, "Receita cadastrada com sucesso", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun callbackError(e: Exception) {
        progressBarIncoming.visibility = View.INVISIBLE
        Toast.makeText(this, "Falha ao cadastrar receita", Toast.LENGTH_LONG).show()
    }


    private fun getReferenceOfTheUser() {
        var user: FirebaseUser? = HandlerFirebase.getFirebaseAuth().currentUser
        if ( user != null ) {
            var userId: String = user.email!!.stringToBase64()
            var database: DatabaseReference = handlerUserDatabaseFirebase.databaseReference
            userReference = database.child("users")
                .child( userId )

            userReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(e: DatabaseError) {
                    print("was cancelled $e")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User? = snapshot.getValue( User::class.java )
                    totalIncoming = user?.totalIncoming
                }
            })
        }
    }

    private fun updateIncoming(value: Double) {
        userReference.child("totalIncoming")
            .setValue( totalIncoming?.plus( value ) )
    }
}
