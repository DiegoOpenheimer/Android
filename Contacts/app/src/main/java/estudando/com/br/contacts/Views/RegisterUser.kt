package estudando.com.br.contacts.Views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import estudando.com.br.contacts.Model.User
import estudando.com.br.contacts.R
import estudando.com.br.contacts.Repository.UserRepository
import estudando.com.br.contacts.Utils.Utils
import estudando.com.br.contacts.Utils.bitmapToBlob
import estudando.com.br.contacts.Utils.byteToBitmap
import estudando.com.br.contacts.hideKeyboard
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register_user.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class RegisterUser : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private val REQUEST_IMAGE_CAPTURE: Int = 1
    private val REQUEST_GALLERY: Int = 2
    private var imageBlob: ByteArray? = null
    private lateinit var user: User
    private var isEditing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mRegisterLayout.setOnTouchListener{
                v: View, m: MotionEvent ->
                v.hideKeyboard()
            true
        }
        fab.setOnClickListener(::validateDate)
        userRepository = UserRepository(applicationContext)
        imageView.setOnClickListener(::alertToChoosePhoto)
        getUser()
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

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun getUser() {
        if (intent.getBooleanExtra("editing", false).equals(true)) {
            this.user = intent.getParcelableExtra("contact")
            name.setText(this.user.name)
            email.setText(this.user.email)
            phone.setText(this.user.phone)
            if (this.user.img != null) {
                imageView.setImageBitmap(this.user.img?.byteToBitmap())
                imageBlob = this.user.img
            } else {
                imageView.setImageResource(R.drawable.person)
            }
            isEditing = true
        }
    }


    private fun validateDate(v: View) {
        val n: String = name.text.toString()
        val e: String = email.text.toString()
        val p: String = phone.text.toString()

        if (n.isBlank() || e.isBlank() || p.isBlank()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Atenção")
            builder.setMessage("Preencha todos os campos")
            builder.setPositiveButton("Ok") {
                dialog, which ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
           saveUser(n, e, p)
        }
    }

    private fun saveUser(name: String, email: String, phone: String) {
        try {
            val user: User = User(name, email, phone, imageBlob)
            if (isEditing.equals(true)) {
                user.id = this.user.id
                userRepository.updateUser(user)
            } else {
                userRepository.saveUser(user)
            }
            clearFields()
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearFields() {
        name.setText("")
        email.setText("")
        phone.setText("")
        name.clearFocus()
        email.clearFocus()
        phone.clearFocus()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun alertToChoosePhoto(v: View) {
        val options: Array<String> = arrayOf("Câmera", "Galeria")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setItems(options, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, index: Int) {
                    when(index) {
                        0 -> setupPermissions()
                        1 -> pickImage()
                    }
                }

            })
            setTitle("Escolha uma das opções")
        }
        val alert: AlertDialog = builder.create()
        alert.show()

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (grantResults[0].equals(PackageManager.PERMISSION_GRANTED)) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(this, "É necessário permissão para está operação", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun pickImage() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && RESULT_OK == resultCode) {
            val imageBitMap = data?.extras?.get("data") as Bitmap
            val uri = Utils.getImageUri(imageBitMap, applicationContext)
            val pathImage = Utils.getRealPathFromUri(applicationContext, uri)
            val newBitmap: Bitmap = Utils.handlerOrientation(imageBitMap, pathImage)
            imageBlob = newBitmap.bitmapToBlob()
            imageView.setImageBitmap(newBitmap)
        } else if(requestCode == REQUEST_GALLERY && RESULT_OK == resultCode) {
            var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
            imageBlob = bitmap.bitmapToBlob()
            imageView.setImageBitmap(bitmap)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
