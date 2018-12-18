package estudando.com.br.organizze.activities

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import estudando.com.br.organizze.R
import kotlinx.android.synthetic.main.activity_login.*
import model.User
import services.HandlerFirebase
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mButton.setOnClickListener( ::validateFields )
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

    private fun validateFields(v: View) {
        if (inputEmail.text.isNullOrBlank() || inputPassword.text.isNullOrBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            doLogin(
                inputEmail.text.toString(), inputPassword.text.toString()
            )
        }
    }

    private fun doLogin(email: String, password: String) {
        val progressDialog: ProgressDialog = ProgressDialog(this).apply {
            setMessage("Aguarde...")
            setCancelable(false)
        }

        progressDialog.show()
        HandlerFirebase.getFirebaseAuth().signInWithEmailAndPassword(email, password)
            .addOnFailureListener(this) { progressDialog.dismiss() }
            .addOnCompleteListener(this) {
                clearFields()
                progressDialog.dismiss()
                if (it.isSuccessful) {
                    Toast.makeText(this, "Sucesso ao logar", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    checkException(it.exception)
                }
            }
    }

    private fun checkException(e: Exception?) {
        lateinit var exception: String
        when(e) {
            is FirebaseAuthInvalidUserException -> exception = "Usuário não cadastrado"
            is FirebaseAuthEmailException -> exception = "Email inválido"
            is FirebaseAuthInvalidCredentialsException -> exception = "Email/Password inválidos"
            else -> exception = "Falha ao fazer login ${e?.message}"
        }
        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
    }

    private fun clearFields() {
        inputEmail.setText("")
        inputPassword.setText("")
        inputEmail.clearFocus()
        inputPassword.clearFocus()
    }
}
