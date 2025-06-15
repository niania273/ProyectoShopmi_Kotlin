package com.proyecto.proyectoshopmi.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.model.response.ProductoResponse

class ProductoAdapter(private val productos: List<ProductoResponse>) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)

        val screenWidth = parent.context.resources.displayMetrics.widthPixels
        view.layoutParams.width = (screenWidth * 0.5).toInt()

        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nomProducto
        holder.estado.text = producto.estProducto.toString()
        holder.descripcion.text = producto.descripcion
        holder.nombreMarca.text = producto.nombreMarca
        holder.precio.text = "S/. ${"%.2f".format(producto.preUni)}"

        /*if (producto.estProducto.equals("Disponible", ignoreCase = true)) {
            estado.text = "Disponible"
            estado.setBackgroundResource(R.drawable.bg_estado)
        } else {
            estado.text = "Inactivo"
            estado.setBackgroundResource(R.drawable.bg_estado_inactivo)
        }*/

        val urlImagen = "http://10.0.2.2:5010/imagenes/productos/${producto.imgProducto}"

        Glide.with(holder.itemView.context)
            .load(urlImagen)
            .placeholder(R.drawable.ic_launcher_foreground) // opcional
            .error(R.drawable.ic_launcher_foreground) // opcional
            .into(holder.imagen)
    }

    override fun getItemCount(): Int = productos.size

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val estado: TextView = view.findViewById(R.id.tvEstado)
        val nombreMarca: TextView = view.findViewById(R.id.tvMarca)
        val nombre: TextView = view.findViewById(R.id.tvNombreProducto)
        val descripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val precio: TextView = view.findViewById(R.id.tvPrecio)
        val imagen: ImageView = view.findViewById(R.id.ivProducto)
    }
}