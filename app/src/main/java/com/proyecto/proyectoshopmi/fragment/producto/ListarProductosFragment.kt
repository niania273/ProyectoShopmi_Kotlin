package com.proyecto.proyectoshopmi.fragment.producto

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.adapter.ProductoAdapter
import com.proyecto.proyectoshopmi.data.model.request.ProductoDetalleRequest
import com.proyecto.proyectoshopmi.data.model.response.ProductoResponse
import com.proyecto.proyectoshopmi.data.service.ProductoService
import com.proyecto.proyectoshopmi.helper.GridSpacingItemDecoration
import com.proyecto.proyectoshopmi.helper.SessionManager

class ListarProductosFragment : Fragment() {

    private var categoriaId: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var etFiltro: EditText
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button

    private val productoService = ProductoService()
    private var productos: List<ProductoResponse> = emptyList()
    private var productosFiltrados: List<ProductoResponse> = emptyList()

    private val productosPorPagina = 6
    private var paginaActual = 1
    private var totalPaginas = 1

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoriaId = arguments?.getInt("categoria_id") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_productos_por_categoria, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etFiltro = view.findViewById(R.id.etFiltro)
        recyclerView = view.findViewById(R.id.recyclerProductos)
        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnSiguiente = view.findViewById(R.id.btnSiguiente)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, 32, true))

        sessionManager = SessionManager(requireContext())

        productoService.obtenerProductosPorCategoria(
            categoriaId,
            onSuccess = { lista ->
                productos = lista
                productosFiltrados = productos
                totalPaginas = (productosFiltrados.size + productosPorPagina - 1) / productosPorPagina
                paginaActual = 1
                mostrarPagina(paginaActual)
                actualizarBotonesPaginacion()
            },
            onError = {
                Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
        )

         etFiltro.addTextChangedListener(object : TextWatcher {
         override fun afterTextChanged(s: Editable?) {
         val texto = s.toString().trim()
         productosFiltrados = if (texto.isEmpty()) {
        productos
         } else {
        productos.filter {
                       it.descripcion.contains(texto, ignoreCase = true)
        }
                }

                totalPaginas = (productosFiltrados.size + productosPorPagina - 1) / productosPorPagina
                paginaActual = 1
                mostrarPagina(paginaActual)
                actualizarBotonesPaginacion()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnAnterior.setOnClickListener {
            if (paginaActual > 1) {
                paginaActual--
                mostrarPagina(paginaActual, animRight = false)
                actualizarBotonesPaginacion()
            }
        }

        btnSiguiente.setOnClickListener {
            if (paginaActual < totalPaginas) {
                paginaActual++
                mostrarPagina(paginaActual, animRight = true)
                actualizarBotonesPaginacion()
            }
        }
    }

    private fun mostrarPagina(pagina: Int, animRight: Boolean = true) {
        Log.d("DEBUG", "Mostrando página $pagina con ${productosFiltrados.size} productos")
        val desde = (pagina - 1) * productosPorPagina
        val hasta = (desde + productosPorPagina).coerceAtMost(productosFiltrados.size)
        val paginaProductos = productosFiltrados.subList(desde, hasta)

        adapter = ProductoAdapter(paginaProductos) { producto ->
            val productoSeleccionado = ProductoDetalleRequest(
                codProducto = producto.codProducto,
                nomProducto = producto.nomProducto,
                imgProducto = producto.imgProducto,
                preUni = producto.preUni,
                stock = producto.stock,
                cantidad = 1,
            )

            val yaExiste = sessionManager.obtenerCarrito().any { it.codProducto == producto.codProducto }

            if (yaExiste) {
                Toast.makeText(requireContext(), "${producto.nomProducto} ya está en el carrito", Toast.LENGTH_SHORT).show()
            } else {
                sessionManager.agregarProductoAlCarrito(productoSeleccionado)
                Toast.makeText(requireContext(), "${producto.nomProducto} agregado al carrito", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.adapter = adapter

        val anim = AnimationUtils.loadAnimation(
            requireContext(),
            if (animRight) R.anim.slide_in_right else R.anim.slide_in_left
        )
        recyclerView.startAnimation(anim)
    }

    private fun actualizarBotonesPaginacion() {
        btnAnterior.isEnabled = paginaActual > 1
        btnSiguiente.isEnabled = paginaActual < totalPaginas
        btnAnterior.alpha = if (paginaActual > 1) 1f else 0.5f
        btnSiguiente.alpha = if (paginaActual < totalPaginas) 1f else 0.5f
    }
}