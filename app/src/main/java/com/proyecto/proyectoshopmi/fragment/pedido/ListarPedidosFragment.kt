package com.proyecto.proyectoshopmi.fragment.pedido

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.adapter.PedidoAdapter
import com.proyecto.proyectoshopmi.data.model.response.PedidoResponse
import com.proyecto.proyectoshopmi.data.service.PedidoService
import com.proyecto.proyectoshopmi.fragment.producto.ActualizarProductoFragment
import com.proyecto.proyectoshopmi.helper.SessionManager

class ListarPedidosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PedidoAdapter
    private var pedidos: List<PedidoResponse> = emptyList()
    private lateinit var pedidoService: PedidoService
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listar_pedidos, container, false)
        recyclerView = view.findViewById(R.id.recyclerPedidos)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pedidoService = PedidoService(requireContext())
        sessionManager = SessionManager(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        cargarPedidos()
    }

    private fun cargarPedidos(){
        pedidoService.listarPedidos(
            onSuccess = { lista ->
                pedidos = lista
                adapter = PedidoAdapter(
                    lista = pedidos,
                    onVerClicked = { codPedido ->
                        val fragment = VerPedidoFragment().apply {
                            arguments = Bundle().apply {
                                putInt("codPedido", codPedido)
                            }
                        }

                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .commit()
                    },
                    onActualizarClicked = { codigoPedido ->
                        val fragment = ActualizarPedidoFragment().apply {
                            arguments = Bundle().apply {
                                putInt("codPedido", codigoPedido)
                            }
                        }

                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.containerPedidos, fragment)
                            .addToBackStack(null)
                            .commit()
                    },
                    onEliminarClicked = { codPedido ->
                        pedidoService.eliminarPedido(
                            codPedido,
                            onSuccess = {
                                Toast.makeText(requireContext(), "Pedido eliminado con éxito", Toast.LENGTH_SHORT).show()
                                cargarPedidos()
                            },
                            onError = { error ->
                                Toast.makeText(requireContext(), "Error al eliminar pedido: $error", Toast.LENGTH_SHORT).show()
                                cargarPedidos()
                            })
                    }
                )
                recyclerView.adapter = adapter
            },
            onError = {
                Toast.makeText(requireContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
            }
        )
    }
}