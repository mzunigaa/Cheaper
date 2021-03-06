package com.example.cheaper

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class LandingActivity : AppCompatActivity() {

    val tag = "[Manati] Landing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        revisarPrimerInicio()

        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.landing_btn_registrarme).setOnClickListener {
            enviarVerificacionActivity()
        }

        findViewById<TextView>(R.id.landing_textView3).setOnClickListener {
            UsuarioRepositorio.cargarSesion(this)
            enviarMain()
        }
    }

    fun revisarPrimerInicio() {
        val sharedPref = this?.getSharedPreferences(getString(R.string.preference_file),Context.MODE_PRIVATE) ?: return
        val primerInicio = sharedPref.getBoolean(getString(R.string.app_name)+"-login-inicio", true)

        Log.d(tag , "Es primer uso: $primerInicio")
        if(primerInicio){
            primerInicio()
        }else{
            revisarSesion()
        }
    }

    fun primerInicio() {
        val sharedPref = this?.getSharedPreferences(getString(R.string.preference_file),Context.MODE_PRIVATE) ?: return

        with (sharedPref.edit()) {
            putBoolean(getString(R.string.app_name)+"-login-inicio", false)
            apply()
        }
        //Para leer todos los preferences
        /*for (pref in sharedPref.all)
            Log.d(tag, "Pref: $pref")*/
    }

    fun enviarVerificacionActivity(){
        val intent = Intent(this , VerificacionActivity::class.java)
        startActivity(intent)
        //finish()
    }

    fun revisarSesion() {
        Log.d(tag,"Revisando sesion...")
        UsuarioRepositorio.cargarSesion(this)
        enviarMain()
    }

    private fun enviarMain() {
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}