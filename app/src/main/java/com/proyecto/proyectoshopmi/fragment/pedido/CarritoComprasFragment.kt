package com.proyecto.proyectoshopmi.fragment.pedido

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.adapter.CarritoAdapter
import com.proyecto.proyectoshopmi.data.model.request.DetallePedidoRequest
import com.proyecto.proyectoshopmi.data.model.response.ProductoCarritoResponse
import com.proyecto.proyectoshopmi.data.model.request.PedidoDetalleRequest
import com.proyecto.proyectoshopmi.data.service.PedidoService
import com.proyecto.proyectoshopmi.databinding.FragmentCarritoComprasBinding
import com.proyecto.proyectoshopmi.fragment.HomeFragment
import com.proyecto.proyectoshopmi.helper.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class CarritoComprasFragment : Fragment() {
    private var _binding: FragmentCarritoComprasBinding? = null
    private lateinit var pedidoService: PedidoService

    private lateinit var sessionManager: SessionManager
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CarritoAdapter
    private lateinit var txtFechaEntrega: EditText
    private lateinit var txtTotal: EditText
    private lateinit var carritoVacio: LinearLayout
    private lateinit var btnComenzarComprar: Button
    private lateinit var layoutResumen: LinearLayout
    private lateinit var btnRegistrarPedido: Button
    private var productosSeleccionados: MutableList<ProductoCarritoResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
        pedidoService = PedidoService(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_carrito_compras, container, false)

        txtFechaEntrega = view.findViewById(R.id.txtFechaEntrega)
        txtTotal = view.findViewById(R.id.txtTotal)
        recycler = view.findViewById(R.id.recyclerCarrito)
        carritoVacio = view.findViewById(R.id.carritoVacio)
        btnComenzarComprar = view.findViewById(R.id.btnComenzarComprar)
        btnRegistrarPedido = view.findViewById(R.id.btnRegistrarPedido)
        layoutResumen = view.findViewById(R.id.layoutResumen)

        var tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale("es", "PE"))
        txtFechaEntrega.setText(dateFormat.format(tomorrow.time))

        txtFechaEntrega.setOnClickListener {
            mostrarDatePickerDialog()
        }

        btnComenzarComprar.setOnClickListener {
            val homeFragment = HomeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerCarrito, homeFragment)
                .addToBackStack(null)
                .commit()
        }

        registrarPedido()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        cargarProductosSeleccionados()

        if (::adapter.isInitialized) {
            adapter.onQuantityChangeListener = { position, newQuantity ->
                calculateAndSetTotal()
            }
        }
    }

    private fun cargarProductosSeleccionados() {
        productosSeleccionados = sessionManager.obtenerCarrito().toMutableList()

        if (productosSeleccionados.isEmpty()) {
            carritoVacio.visibility = View.VISIBLE
            recycler.visibility = View.GONE
            layoutResumen.visibility = View.GONE
        } else {
            carritoVacio.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            layoutResumen.visibility = View.VISIBLE
            adapter = CarritoAdapter(productosSeleccionados)
            recycler.adapter = adapter
            calculateAndSetTotal()
        }
    }

    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentText = txtFechaEntrega.text.toString()
        if (currentText.isNotEmpty()) {
            try {
                val dateFormatParse = SimpleDateFormat("MMM dd, yyyy", Locale("es", "PE"))
                val dateFromEditText = dateFormatParse.parse(currentText)
                if (dateFromEditText != null) {
                    calendar.time = dateFromEditText
                    year.let { calendar.get(Calendar.YEAR) }
                    month.let { calendar.get(Calendar.MONTH) }
                    day.let { calendar.get(Calendar.DAY_OF_MONTH) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale("es", "PE"))
                txtFechaEntrega.setText(dateFormat.format(selectedCalendar.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun calculateAndSetTotal() {
        var totalAmount = 0.0
        for (product in productosSeleccionados) {
            totalAmount += product.preUni * product.cantidad
        }
        val formattedTotal = String.format(Locale("es", "PE"), "S/. %.2f", totalAmount)
        txtTotal.setText(formattedTotal)
    }

    private fun registrarPedido(){
        btnRegistrarPedido.setOnClickListener {
            val fechaEntregaStr = txtFechaEntrega.text.toString()
            val totalStr = txtTotal.text.toString().replace("S/. ", "").replace(",", ".")
            val usuarioLogeado = sessionManager.obtenerUsuario()
            if (usuarioLogeado == null) {
                Toast.makeText(context, "Usuario no logeado. Por favor, inicie sesión.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (productosSeleccionados.isEmpty()) {
                Toast.makeText(context, "El carrito está vacío. Agrega productos antes de registrar un pedido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val parsedTotal = totalStr.toDoubleOrNull()
            if (parsedTotal == null) {
                Toast.makeText(context, "El total no es un número válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdfEntrada = SimpleDateFormat("MMM dd,yyyy", Locale("es", "PE"))
            val sdfSalida = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdfSalida.timeZone = TimeZone.getTimeZone("UTC")

            var fechaPedidoDate: Date? = null
            var formattedFechaPedidoString: String? = null

            try {
                fechaPedidoDate = sdfEntrada.parse(fechaEntregaStr)
                if (fechaPedidoDate == null) {
                    Toast.makeText(context, "Error al parsear la fecha de entrega.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                formattedFechaPedidoString = sdfSalida.format(fechaPedidoDate)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Formato de fecha de entrega inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val detallesPedido = productosSeleccionados.map { producto ->
                DetallePedidoRequest(
                    codProducto = producto.codProducto,
                    cantidad = producto.cantidad,
                    preUni = producto.preUni,
                )
            }
            val pedidoRequest = PedidoDetalleRequest(
                codUsuario = usuarioLogeado.id,
                fecPed = formattedFechaPedidoString,
                precioTotal = parsedTotal,
                DetallePedido = detallesPedido
            )
            Log.d("PedidoRequest", "$pedidoRequest")
            pedidoService.registrarPedido(
                pedidoRequest,
                onSuccess = { pedidoResponse ->
                    Toast.makeText(context, "Pedido registrado con éxito: ${pedidoResponse}", Toast.LENGTH_LONG).show()
                    sessionManager.vaciarCarrito()
                    cargarProductosSeleccionados()
                },
                onError = { error ->
                    Toast.makeText(context, "Error al registrar pedido: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}