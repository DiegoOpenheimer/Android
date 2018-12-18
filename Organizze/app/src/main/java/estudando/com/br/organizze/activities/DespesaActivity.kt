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
import kotlinx.android.synthetic.main.activity_despesa.*
import model.Movement
import model.User
import services.HandlerFirebase
import services.HandlerUserDatabaseFirebase

class DespesaActivity : AppCompatActivity() {

    private val handlerUserDatabaseFirebase: HandlerUserDatabaseFirebase = HandlerUserDatabaseFirebase()
    private var totalExpense: Double? = 0.0
    private lateinit var userReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_despesa)
        inputDate.setText(DateCustom.getCurrentDateFormatted())
        fab.setOnClickListener(::validateDate)
        getReferenceOfTheUser()
    }

    private fun validateDate(v: View) {
        val value: String = valueExpense.text.toString()
        val date: String = inputDate.text.toString()
        val category: String = inputCategory.text.toString()
        val description: String = inputDescription.text.toString()
        if ( value.isNullOrBlank() || date.isNullOrBlank() || category.isNullOrBlank() || description.isNullOrBlank() ) {
            Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_LONG).show()
        } else {
           saveExpense(value, date, category, description)
        }
    }

    private fun saveExpense(value: String, date: String, category: String, description: String) {
        progressBar.visibility = View.VISIBLE
        val movement: Movement = Movement()
        movement.category = category
        movement.date = date
        movement.value = value.toDouble()
        movement.description = description
        movement.type = "d"
        handlerUserDatabaseFirebase.saveMovement(movement, ::callbackSuccess, ::callBackError)
        updateExpense( value.toDouble() )
    }

    private fun callbackSuccess() {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, "Despesa cadastrada com sucesso", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun callBackError(e: Exception) {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, "Falha ao cadastrar despesa", Toast.LENGTH_LONG).show()
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
                    totalExpense = user?.totalExpenditure
                }
            })
        }
    }

    private fun updateExpense(value: Double) {
        userReference.child("totalExpenditure")
            .setValue( totalExpense?.plus( value ) )
    }
}
