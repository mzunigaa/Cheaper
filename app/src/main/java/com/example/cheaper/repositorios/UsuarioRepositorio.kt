package com.example.cheaper.repositorios

import android.content.Context
import android.util.Log
import com.example.cheaper.model.Product
import com.example.cheaper.model.ProductoFavorito
import com.example.cheaper.model.Usuario
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

object UsuarioRepositorio {

    val tag = "[Manati] UsuarioRep"

    lateinit var usuarioLogueado: Usuario
    lateinit var authUsuario: FirebaseUser

    fun obtenerUsuarioPorTelefono(telefono:String){

    }

    fun usuarioEstaLogueado():Boolean {
        return if(::usuarioLogueado.isInitialized){
            usuarioLogueado.id != null
        } else false
    }

    fun crearNuevoUsuario(usuarioNuevo:Usuario, context: Context){
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.usuariosCollection).document(usuarioNuevo?.id!!)
            .set(usuarioNuevo)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "Usuario creado exitosamente.")
                usuarioLogueado = usuarioNuevo
                guardarSesion(context)
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error al crear el nuevo usuario.", e)
            }
    }

    fun actualizarUsuario(usuario: Usuario){

       val db = Firebase.firestore
       val docRef = db.collection(RepositorioConstantes.usuariosCollection).
       document(usuario.id.toString()).set(usuario).addOnSuccessListener {
           Log.d(tag, "Usuario actualizado exitosamente.")
       }.addOnFailureListener {e ->
           Log.w(tag, "Error al actualizar el usuario.", e)
       }
    }

    fun buscarUsuarioPorId(uid: String){
        val db = Firebase.firestore
        val docRef = db.collection(RepositorioConstantes.usuariosCollection).document(uid)
        var usuario = docRef.get().addOnSuccessListener {
            Log.d(tag,"Usuario obtenido. ${it.toObject<Usuario>()}")
        }
    }

    fun registrarProductoFavorito(usuario: Usuario, product: Product){
        val db = Firebase.firestore
        var nuevoProductoFavorito = ProductoFavorito(
            product.id,
            product.nombre,
            true
        )
        db.collection(RepositorioConstantes.usuariosCollection).document(usuario?.id!!)
            .collection(RepositorioConstantes.usuariosCollectionProductosFavoritos)
            .document(product?.id!!)
            .set(nuevoProductoFavorito)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "Producto favorito agregado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error al agregar producto favorito.", e)
            }

        usuarioLogueado.productosFavoritos!!.put(nuevoProductoFavorito?.id!!,
            nuevoProductoFavorito)
    }

    fun removerProductoFavorito(usuario: Usuario, product: Product){
        val db = Firebase.firestore
        var nuevoProductoFavorito = ProductoFavorito(
            product.id,
            product.nombre,
            false
        )
        usuarioLogueado.productosFavoritos!!.put(nuevoProductoFavorito?.id!!,
            nuevoProductoFavorito)
        db.collection(RepositorioConstantes.usuariosCollection).document(usuario?.id!!)
            .collection(RepositorioConstantes.usuariosCollectionProductosFavoritos)
            .document(product?.id!!)
            .set(nuevoProductoFavorito)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "Producto favorito deshabilitado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error al agregar producto favorito.", e)
            }
    }

    fun cargarSesion(context: Context) {
        Log.d(tag,"Cargando sesion...")
        val sharedPref = context.getSharedPreferences(RepositorioConstantes.sharedPreferenceFile,Context.MODE_PRIVATE) ?: return

        val appName = RepositorioConstantes.appName
        val usuarioId = sharedPref.getString(appName+"-login-id", "")
        if(usuarioId!=""){
            usuarioLogueado = Usuario(
                usuarioId,
                sharedPref.getString(appName+"-login-nombre", ""),
                sharedPref.getString(appName+"-login-apellido", ""),
                sharedPref.getString(appName+"-login-telefono", ""),
                sharedPref.getString(appName+"-login-foto", "")
            )
            Log.d(tag, "Usuario logueado")
            Log.d(tag, usuarioLogueado.toString())
            cargarProductosFavoritos()
        }
        else
            Log.d(tag,"No hay usuario logueado.")
    }

    fun cerrarSesion(context: Context){
        val sharedPref = context.getSharedPreferences(RepositorioConstantes.sharedPreferenceFile, Context.MODE_PRIVATE) ?: return

        with (sharedPref.edit()) {
            remove(RepositorioConstantes.appName+"-login-id")
            remove(RepositorioConstantes.appName+"-login-nombre")
            remove(RepositorioConstantes.appName+"-login-apellido")
            remove(RepositorioConstantes.appName+"-login-telefono")
            remove(RepositorioConstantes.appName+"-login-foto")
            apply()
        }
        Firebase.auth.signOut()
        usuarioLogueado = Usuario()
    }

    fun guardarSesion(context: Context){
        Log.d(tag, "Guardando sesion...")
        val sharedPref = context.getSharedPreferences(RepositorioConstantes.sharedPreferenceFile,Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(RepositorioConstantes.appName+"-login-id", usuarioLogueado.id)
            putString(RepositorioConstantes.appName+"-login-nombre", usuarioLogueado.nombre)
            putString(RepositorioConstantes.appName+"-login-apellido", usuarioLogueado.apellido)
            putString(RepositorioConstantes.appName+"-login-telefono", usuarioLogueado.telefono)
            putString(RepositorioConstantes.appName+"-login-foto", usuarioLogueado.foto)
            apply()
        }
    }

    fun cargarProductosFavoritos() {
        usuarioLogueado.productosFavoritos = HashMap()
        val db = Firebase.firestore
        db.collection(RepositorioConstantes.usuariosCollection).document(usuarioLogueado?.id!!)
        .collection(RepositorioConstantes.usuariosCollectionProductosFavoritos)
        .get()
        .addOnSuccessListener { documentReference ->
            for (document in documentReference) {
                usuarioLogueado.productosFavoritos!!.put(document.id,
                    document.toObject<ProductoFavorito>()!!)
                Log.d(tag, "${document.id} => ${document.data}")
            }
            Log.d(tag, "Productos favoritos cargados exitosamente: ${documentReference.size()}")
        }
        .addOnFailureListener { e ->
            Log.w(tag, "Error al cargar productos favoritos.", e)
        }
    }

}