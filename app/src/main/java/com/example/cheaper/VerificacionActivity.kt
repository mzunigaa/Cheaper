package com.example.cheaper

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.FirebaseException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class VerificacionActivity : AppCompatActivity() {

    // this stores the phone number of the user
    var number : String =""

    // create instance of firebase auth
    lateinit var auth: FirebaseAuth

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    // we will use this to match the sent otp from firebase
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    val tag = "[Manati] Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verficacion)

        auth=FirebaseAuth.getInstance()

        firebaseAnalytics = Firebase.analytics

        // start verification on click of the button
        findViewById<Button>(R.id.login).setOnClickListener {
            ingresarNumeroTelefono()
        }


        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(tag , "Verficación exitosa.")
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(tag , "Problema con verificación:  $e")
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(tag,"Código enviado. ID de verificación: $verificationId")
                storedVerificationId = verificationId
                resendToken = token
                //this@LandingActivity.enableUserManuallyInputCode()


                iniciarVerificacion()
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                Log.d(tag,"Código no se puedo obtener automaticamente, verification ID: $verificationId")
            }
        }
    }

    private fun ingresarNumeroTelefono() {
        number = findViewById<EditText>(R.id.et_otp).text.trim().toString()

        // get the phone number from edit text and append the country cde with it
        if (number.isNotEmpty()){
            //Para automatizar el login con un numero ficticio registrado en Firebase
            number = "+1 650-555-3434"

            // Para no tener que hacerlo manual con un usuario real, se crea un 'dumb' session
            // pero no carga un usuario real
            //val code = "123456"
            //auth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(number, code)

            //number = "$number"
            sendVerificationCode(number)

        }else{
            Toast.makeText(this,"Ingresar número telefónico", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationCode(number: String) {
        Log.d(tag , "Phone number $number")
        // Force reCAPTCHA flow
        //auth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true)

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(3, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d(tag, "Autenticación iniciada...")
    }

    fun iniciarVerificacion(){

        findViewById<EditText>(R.id.et_otp).setText("")
        findViewById<EditText>(R.id.tv_otp).setText("Ingresar código SMS")
        //findViewById<EditText>(R.id.login).setText("login")
        // fill otp and call the on click on button
        findViewById<Button>(R.id.login).setOnClickListener {
            val otp = findViewById<EditText>(R.id.et_otp).text.trim().toString()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Ingresar código de verificación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    siguienteActivity()
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.d("Login log", "Invalid OTP")
                        Toast.makeText(this,"Código de verificación inválido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun enviarMain(){
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun enviarRegistrar(){
        //TODO
        Log.d("Login log", "Enviando a registrar")
        var authUsuario = Firebase.auth.currentUser!!
        var usuario = Usuario(
            authUsuario?.phoneNumber!!,
            "Monica",
            "Zuniga",
            authUsuario?.uid!!
        )
        UsuarioRepositorio.crearNuevoUsuario(usuario)
        //enviarMain()
    }

    fun siguienteActivity(){
        GlobalScope.launch(Dispatchers.IO) {
            UsuarioRepositorio.cargarUsuarioLogueado()
            guardarSesion()
            if(UsuarioRepositorio.usuarioLogueado ==null){
//                withContext(Dispatchers.Main){
//
//                    val intent = Intent(this@VerificacionActivity , MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
            }else{
//                withContext(Dispatchers.Main){
//
//                    val intent = Intent(this@VerificacionActivity , MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
            }
        }
        enviarMain()
    }

    fun guardarSesion(){
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getString(R.string.app_name)+"-login-id", UsuarioRepositorio.usuarioLogueado.id)
            putString(getString(R.string.app_name)+"-login-nombre", UsuarioRepositorio.usuarioLogueado.nombre)
            putString(getString(R.string.app_name)+"-login-apellido", UsuarioRepositorio.usuarioLogueado.apellido)
            putString(getString(R.string.app_name)+"-login-telefono", UsuarioRepositorio.usuarioLogueado.telefono)
            putString(getString(R.string.app_name)+"-login-foto", UsuarioRepositorio.usuarioLogueado.foto)
            apply()
        }
    }

    fun cerrarSesion(){
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            remove(getString(R.string.app_name)+"-login-id")
            remove(getString(R.string.app_name)+"-login-nombre")
            remove(getString(R.string.app_name)+"-login-apellido")
            remove(getString(R.string.app_name)+"-login-telefono")
            remove(getString(R.string.app_name)+"-login-foto")
            apply()
        }
    }

}