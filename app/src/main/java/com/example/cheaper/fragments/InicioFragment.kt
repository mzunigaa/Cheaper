package com.example.cheaper.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheaper.ProductoAdapter
import com.example.cheaper.R
import com.example.cheaper.model.Product
import com.google.firebase.firestore.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var productRecyclerView : RecyclerView
private lateinit var productArrayList : ArrayList<Product>
private lateinit var myAdapter : ProductoAdapter
private lateinit var db : FirebaseFirestore
private lateinit var viewOfLayout: View
private lateinit var searchViewProduct : androidx.appcompat.widget.SearchView
private var sTextSearch:String=""

/**
 * A simple [Fragment] subclass.
 * Use the [InicioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InicioFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_inicio, container, false)
        productRecyclerView = viewOfLayout.findViewById(R.id.productsListInicio)
        productRecyclerView.layoutManager = LinearLayoutManager(this.context)
        productArrayList = arrayListOf()
        myAdapter = ProductoAdapter(productArrayList)
        productRecyclerView.adapter = myAdapter
        searchViewProduct = viewOfLayout.findViewById(R.id.search_view_producto)
        myAdapter.notifyDataSetChanged()
        searching(searchViewProduct)
        getProductos()
        //EventChangeListener()
        return viewOfLayout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InicioFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InicioFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }

            }
    }


    private fun getProductos(){
        if(sTextSearch.isEmpty()){
            db = FirebaseFirestore.getInstance()
            db.collection("productos").
            orderBy("nombre").
            get().
            addOnSuccessListener { documents ->
                productArrayList.clear()
                var productos = ArrayList<Product>()
                productos = arrayListOf()
                for (document in documents) {
                    var producto = document.toObject(Product::class.java)
                    producto.id = document.id
                    productos.add(producto)
                }
                productArrayList.addAll(productos)
                productRecyclerView.adapter = ProductoAdapter(productArrayList)
            }
                .addOnFailureListener{ exception ->
                    Log.w(TAG, "Error getting products: ", exception)
                }
        } else {
            db = FirebaseFirestore.getInstance()
            db.collection("productos").
                //whereEqualTo("nombre", sTextSearch).
                //whereLessThan("nombre", sTextSearch).
                orderBy("nombre").
                orderBy("marca").
                startAt(sTextSearch.uppercase()).
                endAt(sTextSearch.lowercase() + "\uf8ff").
                get().
                addOnSuccessListener { documents ->
                    productArrayList.clear()
                    var productos = ArrayList<Product>()
                    productos = arrayListOf()
                    for (document in documents) {
                    var producto = document.toObject(Product::class.java)
                    producto.id = document.id
                        productos.add(producto)
                }
                    productArrayList.addAll(productos)
                    productRecyclerView.adapter = ProductoAdapter(productArrayList)
                    if(productArrayList.size == 0){
                        Log.w(TAG, "No se encontraron productos. ")
                    }
                }
                    .addOnFailureListener{ exception ->
                        Log.w(TAG, "Error getting products: ", exception)
                    }
        }

    }


    private fun EventChangeListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("productos").whereEqualTo("marca","Vans").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?, error: FirebaseFirestoreException?
            ) {

                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (dc : DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){
                        val producto = dc.document.toObject(Product::class.java)
                        producto.id = dc.document.id
                        productArrayList.add(producto)
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun searching(search: androidx.appcompat.widget.SearchView) {
        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                sTextSearch=newText!!
                getProductos()
                return true
            }
        })
    }


}