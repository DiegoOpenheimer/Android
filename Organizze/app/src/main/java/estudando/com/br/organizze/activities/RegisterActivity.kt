package estudando.com.br.organizze.activities

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import estudando.com.br.organizze.R
import kotlinx.android.synthetic.main.activity_register.*
import model.User
import services.HandlerFirebase
import services.HandlerUserDatabaseFirebase
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    val handlerDatabaseService: HandlerUserDatabaseFirebase = HandlerUserDatabaseFirebase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.setDisplayHomeAsUpEnabled( true )
        buttonRegister.setOnClickListener( ::validateForm )
    }

    private fun validateForm(v: View) {
        v.hideKeyboard()
        if (inputName.text.isNullOrBlank() || inputEmail.text.isNullOrBlank() || inputPassword.text.isNullOrBlank()) {
            Toast.makeText(this, "Preencha os campos corretamente", Toast.LENGTH_LONG).show()
        } else {
            var user: User = User()
            user.name = inputName.text.toString()
            user.email = inputEmail.text.toString()
            user.password = inputPassword.text.toString()
            createAccount( user )
        }
    }

    private fun createAccount(user: User) {
        val progressDialog: ProgressDialog = ProgressDialog(this)
        progressDialog.apply {
            setMessage("Aguarde...")
            setCancelable(false)
        }

        progressDialog.show()
        HandlerFirebase.getFirebaseAuth().createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(
                this
            ) {
                progressDialog.dismiss()
                if (it.isSuccessful) {
                    finish()
                    Toast.makeText(this, "Conta criada com sucesso", Toast.LENGTH_SHORT).show()
                    clearFields()
                    handlerDatabaseService.saveUser(user)
                } else {
                    checkException(it.exception)
                }
            }
    }

    private fun checkException(exception: Exception?) {
        var message: String? = null
        when(exception) {
            is FirebaseAuthWeakPasswordException -> message = "Digíte uma senha mais forte"
            is FirebaseAuthInvalidCredentialsException -> message = "Digíte um email válido"
            is FirebaseAuthUserCollisionException -> message = "Essa conta já foi cadastrada"
            else -> {
                message = "Erro ao cadastrar usuário ${exception?.message}"
                exception?.printStackTrace()
            }
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun clearFields() {
        inputName.setText("")
        inputEmail.setText("")
        inputPassword.setText("")
        inputName.clearFocus()
        inputEmail.clearFocus()
        inputPassword.clearFocus()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
