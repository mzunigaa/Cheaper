package com.example.cheaper.fragments

import android.content.Context
import android.content.Intent.getIntent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.cheaper.MainActivity
import com.example.cheaper.R
import com.example.cheaper.repositorios.UsuarioRepositorio
import kotlinx.android.synthetic.main.fragment_perfil.*
import android.content.Intent




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {
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
        mView=inflater.inflate(R.layout.fragment_perfil,container,false)
        cargarPerfil()
        mView?.findViewById<TextView>(R.id.txtCerrarSesion)?.setOnClickListener {
            cerrarSesion()
        }
        return mView
    }


    fun cerrarSesion(){
        Log.d("[Manati] PerfilFragment","Cerrando Sesion.")
        UsuarioRepositorio.cerrarSesion(this?.requireActivity())
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        this?.requireActivity().finish()
    }

    fun cargarPerfil(){

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}