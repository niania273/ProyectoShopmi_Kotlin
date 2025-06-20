package com.proyecto.proyectoshopmi.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.adapter.ProductoAdapter
import com.proyecto.proyectoshopmi.data.model.request.ProductoDetalleRequest
import com.proyecto.proyectoshopmi.data.service.ProductoService
import com.proyecto.proyectoshopmi.fragment.autenticacion.LoginFragment
import com.proyecto.proyectoshopmi.fragment.autenticacion.RegisterFragment
import com.proyecto.proyectoshopmi.fragment.producto.ActualizarProductoFragment
import com.proyecto.proyectoshopmi.fragment.producto.VerProductoFragment
import com.proyecto.proyectoshopmi.helper.SessionManager

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private val productoService = ProductoService()
    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private var productosSize = 0
    private val intervalo = 3000L

    private val scrollRunnable = object : Runnable {
        override fun run() {
            if (productosSize == 0) return

            currentIndex += 2
            if (currentIndex >= productosSize) {
                currentIndex = 0
            }
            recyclerView.smoothScrollToPosition(currentIndex)
            handler.postDelayed(this, intervalo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerViewProductos)
        loginButton = view.findViewById(R.id.loginButton)
        registerButton = view.findViewById(R.id.registerButton)

        // Configurar RecyclerView
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager


        // Evita que el usuario haga scroll manual
        recyclerView.setOnTouchListener { _, _ -> true }

        // Acciones de los botones
        loginButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        registerButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        productoService.obtenerTop5MasBaratos(
            onSuccess = { productos ->
                adapter = ProductoAdapter(
                    productos = productos,
                    onAddToCartClicked = { producto ->
                        val productoSeleccionado = ProductoDetalleRequest(
                            codProducto = producto.codProducto,
                            nomProducto = producto.nomProducto,
                            imgProducto = producto.imgProducto,
                            preUni = producto.preUni,
                            stock = producto.stock,
                            cantidad = 1,
                        )

                        val sessionManager = SessionManager(requireContext())
                        val yaExiste = sessionManager.obtenerCarrito().any { it.codProducto == producto.codProducto }

                        if (yaExiste) {
                            Toast.makeText(requireContext(), "${producto.nomProducto} ya está en el carrito", Toast.LENGTH_SHORT).show()
                        } else {
                            sessionManager.agregarProductoAlCarrito(productoSeleccionado)
                            Toast.makeText(requireContext(), "${producto.nomProducto} agregado al carrito", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onViewProductClicked = { producto ->
                        val bundle = Bundle().apply {
                            putInt("codProducto", producto.codProducto)
                            putString("nomProducto", producto.nomProducto)
                            putString("imgProducto", producto.imgProducto)
                            putDouble("preUni", producto.preUni ?: 0.0)
                            putInt("stock", producto.stock ?: 0)
                            putString("descripcion", producto.descripcion)
                            putString("nombreMarca", producto.nombreMarca)
                        }
                        val fragment = VerProductoFragment().apply { arguments = bundle }

                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .commit()
                    },
                    onActualizarClicked = { producto ->
                        val bundle = Bundle().apply {
                            putInt("codProducto", producto.codProducto)
                            putString("nomProducto", producto.nomProducto)
                            putString("imgProducto", producto.imgProducto)
                            putDouble("preUni", producto.preUni ?: 0.0)
                            putInt("stock", producto.stock ?: 0)
                            putString("descripcion", producto.descripcion)
                            putString("nombreMarca", producto.nombreMarca)
                        }
                        val fragment = ActualizarProductoFragment().apply { arguments = bundle }

                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .commit()
                    },
                    onDesactivarClicked = { producto ->
                        Toast.makeText(requireContext(), "Confirmar eliminación de ${producto.nomProducto}", Toast.LENGTH_SHORT).show()
                        // productoService.desactivarProducto(producto.codProducto, onSuccess = { /* ... */ }, onError = { /* ... */ })
                    }
                )
                recyclerView.adapter = adapter
                productosSize = productos.size
                handler.postDelayed(scrollRunnable, intervalo)
            },
            onError = { errorMsg ->
                Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
            }
        )
    }
}