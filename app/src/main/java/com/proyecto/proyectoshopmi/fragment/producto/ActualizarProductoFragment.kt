package com.proyecto.proyectoshopmi.fragment.producto

import android.os.Bundle
import android.util.Log // Import for Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast // For showing a toast if data is missing
import com.google.android.material.button.MaterialButton
import com.proyecto.proyectoshopmi.R
// No need for ProductoResponse import here if not receiving the object

class ActualizarProductoFragment : Fragment() {

    // Tag for logging
    private val TAG = "ActualizarProductoFrag"

    // Properties to hold the received product data
    private var codProducto: Int? = null
    private var nomProducto: String? = null
    private var imgProducto: String? = null
    private var preUni: Double? = null
    private var stock: Int? = null
    private var descripcion: String? = null
    private var nombreMarca: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the individual arguments here
        arguments?.let {
            codProducto = it.getInt("codProducto", 0) // Default to 0 if not found
            nomProducto = it.getString("nomProducto")
            imgProducto = it.getString("imgProducto")
            preUni = it.getDouble("preUni", 0.0) // Default to 0.0
            stock = it.getInt("stock", 0)
            descripcion = it.getString("descripcion")
            nombreMarca = it.getString("nombreMarca")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_actualizar_producto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (codProducto != null && nomProducto != null) {
            Log.d(TAG, "Received Product for Update (Individual Fields):")
            Log.d(TAG, "  CodProducto: $codProducto")
            Log.d(TAG, "  NomProducto: $nomProducto")
            Log.d(TAG, "  ImgProducto: $imgProducto")
            Log.d(TAG, "  PreUni: $preUni")
            Log.d(TAG, "  Stock: $stock")
            Log.d(TAG, "  Descripcion: $descripcion")
            Log.d(TAG, "  NombreMarca: $nombreMarca")

        } else {
            Log.e(TAG, "No essential product data received in ActualizarProductoFragment!")
            Toast.makeText(requireContext(), "Error al cargar datos del producto.", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            codProducto: Int,
            nomProducto: String,
            imgProducto: String,
            preUni: Double,
            stock: Int,
            descripcion: String,
            nombreMarca: String
        ) = ActualizarProductoFragment().apply {
            arguments = Bundle().apply {
                putInt("codProducto", codProducto)
                putString("nomProducto", nomProducto)
                putString("imgProducto", imgProducto)
                putDouble("preUni", preUni)
                putInt("stock", stock)
                putString("descripcion", descripcion)
                putString("nombreMarca", nombreMarca)
            }
        }
    }
}