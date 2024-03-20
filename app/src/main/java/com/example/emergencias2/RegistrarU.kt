package com.example.emergencias2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Objects

public class RegistrarU : AppCompatActivity() {

    private lateinit var mEditTextNombre: EditText
    private lateinit var mEditTextTelefono: EditText
    private lateinit var mEditTextDomicilio: EditText
    private lateinit var mEditTextCorreo: EditText
    private lateinit var mEditTextContrasena: EditText
    private lateinit var mEditTextRecContra: EditText
    private  lateinit var mButtonGuardar: Button

    private var nombre: String = ""
    private var telef: String = ""
    private var domi: String = ""
    private var correo: String = ""
    private var contra: String = ""
    private var concontra: String = ""

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_u)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mEditTextNombre = findViewById(R.id.idTextInpEditNombre)
        mEditTextTelefono = findViewById(R.id.idTextInpEdiTelef)
        mEditTextDomicilio = findViewById(R.id.idTextInpEditDairec)
        mEditTextCorreo = findViewById(R.id.idTextInpEditCorreo)
        mEditTextContrasena = findViewById(R.id.idTextInpEditPass)
        mEditTextRecContra = findViewById(R.id.idTextInpEditConfirm)
        mButtonGuardar = findViewById(R.id.idButtonGuardarReg)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference()

        mButtonGuardar.setOnClickListener { view ->
           nombre = mEditTextNombre.text.toString()
           telef = mEditTextTelefono.text.toString()
           domi = mEditTextDomicilio.text.toString()
           correo = mEditTextCorreo.text.toString()
           contra = mEditTextContrasena.text.toString()
           concontra = mEditTextRecContra.text.toString()

            if (!nombre.isEmpty() && !telef.isEmpty() && !domi.isEmpty() && !correo.isEmpty() && !contra.isEmpty() && !concontra.isEmpty()){

                if (contra.length >= 6) {
                    if (contra == concontra) {
                        registerUser()
                    }else{
                        Toast.makeText(this, "La contraseña y su confirmación no coinciden", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(){
        mAuth.createUserWithEmailAndPassword(correo, contra)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // El usuario se registró exitosamente

                    val map = mutableMapOf<String, Any>()
                    map["name"] = nombre
                    map["telefono"] = telef
                    map["domicilio"] = domi
                    map["correo"] = correo
                    map["contrasena"] = contra
                    map["confcontrasena"] = concontra

                    val id = mAuth.currentUser?.uid

                    mDatabase.child("Users").child(id!!).setValue(map)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                startActivity(Intent(this@RegistrarU, Menu::class.java))
                                finish()
                                // El usuario se registró exitosamente en la base de datos
                            } else {
                                // Si falla la escritura en la base de datos, muestra un mensaje de error
                                Toast.makeText(this, "Error al escribir en la base de datos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    val user = mAuth.currentUser
                    // Puedes realizar acciones adicionales aquí después de que el usuario se registre
                } else {
                    // Si el registro falla, muestra un mensaje de error
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
    }

}