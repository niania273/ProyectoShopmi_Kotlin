package com.proyecto.proyectoshopmi.data.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.proyecto.proyectoshopmi.R
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.proyectoshopmi.data.model.response.PedidoResponse
import kotlin.random.Random

class PedidoAdapter(
    private val lista: List<PedidoResponse>,
    private val onVerClicked: ((codPedido: Int) -> Unit),
    private val onActualizarClicked: ((codPedido: Int) -> Unit)? = null,
    private val onEliminarClicked: ((codPedido: Int) -> Unit)? = null
    ) :
    RecyclerView.Adapter<PedidoAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNumeroPedido: TextView = itemView.findViewById(R.id.txtNumeroPedido)
        val txtNombreCliente: TextView = itemView.findViewById(R.id.txtNombreCliente)
        val txtFechaPedido: TextView = itemView.findViewById(R.id.txtFechaPedido)
        val txtPrecioTotalPedido: TextView = itemView.findViewById(R.id.txtPrecioTotalPedido)
        val txtEstadoPedido: TextView = itemView.findViewById(R.id.txtEstadoPedido)
        val btnVer: ImageButton = itemView.findViewById(R.id.btnVer)
        val btnActualizar: ImageButton = itemView.findViewById(R.id.btnActualizar)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val pedido = lista[position]
        holder.txtNumeroPedido.text = "NRO. ${pedido.codPedido}"
        holder.txtNombreCliente.text = pedido.cliente
        holder.txtFechaPedido.text = pedido.fecPedido
        holder.txtPrecioTotalPedido.text = "S/. %.2f".format(pedido.precioTotal)
        holder.txtEstadoPedido.text = pedido.nomEstado

        cambiarBackgroundTint(holder.txtEstadoPedido)

        holder.btnVer.setOnClickListener {
            onVerClicked(pedido.codPedido)
        }
        holder.btnActualizar.setOnClickListener {
            onActualizarClicked?.invoke(pedido.codPedido)
        }
        holder.btnEliminar.setOnClickListener {
            onEliminarClicked?.invoke(pedido.codPedido)
        }
    }

    override fun getItemCount(): Int = lista.size

    private fun cambiarBackgroundTint(textView: TextView){
        val random = Random.Default
        var color: Int
        var esColorMedio: Boolean

        do {
            val red = random.nextInt(256)
            val green = random.nextInt(256)
            val blue = random.nextInt(256)
            color = Color.rgb(red, green, blue)
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(color, hsl)
            esColorMedio = hsl[2] in 0.3f..0.7f
        } while (!esColorMedio)
        if (textView.background != null) {
            textView.background.setTint(color)
        }
    }
}