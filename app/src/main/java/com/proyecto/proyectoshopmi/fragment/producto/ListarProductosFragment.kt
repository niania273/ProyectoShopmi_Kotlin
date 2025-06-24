package com.proyecto.proyectoshopmi.fragment.producto

import GridSpacingItemDecoration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.button.MaterialButton
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.adapter.ProductoAdapter
import com.proyecto.proyectoshopmi.data.model.response.ProductoCarritoResponse
import com.proyecto.proyectoshopmi.data.model.response.ProductoResponse
import com.proyecto.proyectoshopmi.data.service.ProductoService
import com.proyecto.proyectoshopmi.helper.SessionManager

class ListarProductosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var etFiltro: EditText
    private lateinit var spinnerFiltro: AutoCompleteTextView
    private lateinit var btnAnterior: MaterialButton
    private lateinit var btnSiguiente: MaterialButton
    private lateinit var tvPagina: TextView
    private lateinit var btnNuevoProducto: MaterialButton
    private lateinit var filterContainer: LinearLayout

    private var todosLosProductos: List<ProductoResponse> = emptyList()
    private var productosFiltrados: List<ProductoResponse> = emptyList()

    private val productosPorPagina = 6
    private var paginaActual = 1
    private var totalPaginas = 1

    private val productoService = ProductoService()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_productos_por_categoria, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etFiltro = view.findViewById(R.id.etFiltro)
        spinnerFiltro = view.findViewById(R.id.spinnerFiltro)
        recyclerView = view.findViewById(R.id.recyclerProductos)
        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnSiguiente = view.findViewById(R.id.btnSiguiente)
        tvPagina = view.findViewById(R.id.tvPagina)
        btnNuevoProducto = view.findViewById(R.id.btnNuevoProducto)
        filterContainer = view.findViewById(R.id.filterContainer)

        sessionManager = SessionManager(requireContext())

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.setHasFixedSize(true)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                btnNuevoProducto.visibility = when {
                    dy > 0 -> View.GONE
                    dy < 0 && firstVisibleItemPosition == 0 && !recyclerView.canScrollVertically(-1) -> View.VISIBLE
                    else -> btnNuevoProducto.visibility
                }
            }
        })

        val opcionesFiltro = listOf("Todo", "Disponibles", "Precio ↑", "Precio ↓")
        val adapterFiltro = ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, opcionesFiltro)
        spinnerFiltro.setAdapter(adapterFiltro)
        spinnerFiltro.setText(opcionesFiltro[0], false)

        spinnerFiltro.setOnItemClickListener { _, _, _, _ -> aplicarFiltros() }

        etFiltro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { aplicarFiltros() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val usuario = sessionManager.obtenerUsuario()
        btnNuevoProducto.visibility = if (usuario?.rolId == 2 || usuario?.rolId == 3) View.VISIBLE else View.GONE

        btnNuevoProducto.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, RegistrarProductoFragment())
                .addToBackStack(null)
                .commit()
        }

        adapter = ProductoAdapter(
            productos = emptyList(),
            onAddToCartClicked = { productoClick ->
                val productoSeleccionado = ProductoCarritoResponse(
                        codProducto = productoClick.codProducto,
                        nomProducto = productoClick.nomProducto,
                        imgProducto = productoClick.imgProducto,
                        preUni = productoClick.preUni,
                        stock = productoClick.stock,
                        cantidad = 1,
                        nomMarca = productoClick.nombreMarca,
                        nomCategoria = productoClick.nomCategoria
                )
                val yaExiste = sessionManager.obtenerCarrito().any {
                    it.codProducto == productoClick.codProducto
                }

                if (yaExiste) {
                    Toast.makeText(requireContext(), "${productoClick.nomProducto} ya está en el carrito", Toast.LENGTH_SHORT).show()
                } else {
                    sessionManager.agregarProductoAlCarrito(productoSeleccionado)
                    Toast.makeText(requireContext(), "${productoClick.nomProducto} agregado al carrito", Toast.LENGTH_SHORT).show()
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
                    putString("nomCategoria", producto.nomCategoria)
                    putBoolean("estProducto", producto.estProducto ?: false)
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
                    putInt("codProducto", producto.codProducto ?: 0)
                    putString("nomProducto", producto.nomProducto)
                    putString("imgProducto", producto.imgProducto)
                    putDouble("preUni", producto.preUni ?: 0.0)
                    putInt("stock", producto.stock ?: 0)
                    putString("descripcion", producto.descripcion)
                    putString("nombreMarca", producto.nombreMarca)
                    putString("nomCategoria", producto.nomCategoria)
                    putBoolean("estProducto", producto.estProducto ?: false)
                }

                val fragment = ActualizarProductoFragment().apply { arguments = bundle }

                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDesactivarClicked = { producto ->
                Toast.makeText(requireContext(), "Eliminar ${producto.nomProducto}", Toast.LENGTH_SHORT).show()
                productoService.cambiarEstadoProducto(producto.codProducto)
            }
        )
        recyclerView.adapter = adapter

        cargarProductos()

        btnAnterior.setOnClickListener {
            if (paginaActual > 1) {
                paginaActual--
                mostrarPagina(paginaActual, animRight = false)
            }
        }

        btnSiguiente.setOnClickListener {
            if (paginaActual < totalPaginas) {
                paginaActual++
                mostrarPagina(paginaActual, animRight = true)
            }
        }
    }

    private fun cargarProductos() {
        productoService.obtenerProductosPorCategoria(
            codCategoria = arguments?.getInt("categoria_id") ?: -1,
            onSuccess = { lista ->
                todosLosProductos = lista
                aplicarFiltros()
            },
            onError = {
                Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun aplicarFiltros() {
        val texto = etFiltro.text.toString().trim()
        val seleccion = spinnerFiltro.text.toString()

        productosFiltrados = todosLosProductos.filter {
            it.nomProducto.contains(texto, ignoreCase = true) || it.descripcion.contains(texto, ignoreCase = true)
        }.let {
            when (seleccion) {
                "Disponibles" -> it.filter { prod -> prod.estProducto == true }
                "Precio ↓" -> it.sortedBy { prod -> prod.preUni }
                "Precio ↑" -> it.sortedByDescending { prod -> prod.preUni }
                else -> it
            }
        }

        totalPaginas = (productosFiltrados.size + productosPorPagina - 1) / productosPorPagina
        if (productosFiltrados.isEmpty()) totalPaginas = 1

        paginaActual = 1
        mostrarPagina(paginaActual)
    }

    private fun mostrarPagina(pagina: Int, animRight: Boolean = true) {
        val desde = (pagina - 1) * productosPorPagina
        val hasta = (desde + productosPorPagina).coerceAtMost(productosFiltrados.size)
        val paginaProductos = productosFiltrados.subList(desde, hasta)

        adapter.updateData(paginaProductos)

        if (paginaProductos.isNotEmpty()) {
            val anim = AnimationUtils.loadAnimation(
                requireContext(),
                if (animRight) R.anim.slide_in_right else R.anim.slide_in_left
            )
            recyclerView.startAnimation(anim)
        } else {
            recyclerView.clearAnimation()
        }

        actualizarBotonesPaginacion()
    }

    private fun actualizarBotonesPaginacion() {
        btnAnterior.isEnabled = paginaActual > 1
        btnSiguiente.isEnabled = paginaActual < totalPaginas

        btnAnterior.alpha = if (btnAnterior.isEnabled) 1f else 0.5f
        btnSiguiente.alpha = if (btnSiguiente.isEnabled) 1f else 0.5f

        tvPagina.text = "$paginaActual / $totalPaginas"
    }
}