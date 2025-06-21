package com.proyecto.proyectoshopmi.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.model.response.ProductoPedidoResponse

class CarritoAdapter(private val lista: List<ProductoPedidoResponse>) :
    RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val etiqueta1: TextView = itemView.findViewById(R.id.etiqueta1)
        val etiqueta2: TextView = itemView.findViewById(R.id.etiqueta2)
        val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecio)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
        val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
        val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = lista[position]

        holder.txtNombre.text = producto.nomProducto
        holder.etiqueta1.text = "Marca X"  // puedes adaptar si hay más info
        holder.etiqueta2.text = "Categoría Y"
        holder.txtPrecio.text = "S/. %.2f".format(producto.preUni)
        holder.txtCantidad.text = producto.cantidad.toString()

        // Cargar imagen con Glide
        Glide.with(holder.itemView.context)
            .load(producto.imgProducto)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgProducto)

        // Aquí puedes agregar lógica para los botones + y -
        holder.btnMas.setOnClickListener {
            // por ahora solo mostrar un log o toast
        }

        holder.btnMenos.setOnClickListener {
            // por ahora solo mostrar un log o toast
        }
    }
}