package com.example.cheaper.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.cheaper.R
import com.example.cheaper.VerificacionActivity
import com.example.cheaper.repositorios.RepositorioConstantes

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NoPerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoPerfilFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView=inflater.inflate(R.layout.fragment_no_perfil,container,false)

        mView.findViewById<Button>(R.id.buttonIniciarSesion)?.setOnClickListener {
            Log.d("[Manati] NoPerfil","Iniciando Sesion.")
            val intent = Intent(context, VerificacionActivity::class.java)
            context?.startActivity(intent)
            this?.requireActivity().finish()
        }

        mView.findViewById<TextView>(R.id.no_perfil_restaurar)?.setOnClickListener {
            restaurarAFabrica()
        }
        // Inflate the layout for this fragment
        return mView
    }

    fun enviarVerificacionActivity(){

    }

    fun restaurarAFabrica(){
        val sharedPref = this.requireActivity().getSharedPreferences(RepositorioConstantes.sharedPreferenceFile, Context.MODE_PRIVATE) ?: return
        val tag = "[Manati] NoPerfil"
        Log.d(tag,"Restaurando.")
        Log.d(tag, "All preferences")
        for (pref in sharedPref.all)
            Log.d(tag, "Pref: ${pref.toString()}")

        with (sharedPref.edit()) {
            remove(RepositorioConstantes.appName+"-login-inicio")
            apply()
        }

        this?.requireActivity().finish()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NoPerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NoPerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}