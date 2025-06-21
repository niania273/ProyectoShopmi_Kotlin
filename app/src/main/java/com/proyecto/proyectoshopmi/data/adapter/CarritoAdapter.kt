package com.proyecto.proyectoshopmi.data.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.model.response.ProductoCarritoResponse
import kotlin.random.Random

class CarritoAdapter(private val lista: MutableList<ProductoCarritoResponse>) :
    RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {
    var onQuantityChangeListener: ((Int, Int) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val txtMarca: TextView = itemView.findViewById(R.id.txtMarca)
        val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecio)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
        val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
        val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        val imagen: ImageView = itemView.findViewById(R.id.imgProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(vista)
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
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = lista[position]

        holder.txtNombre.text = producto.nomProducto
        holder.txtMarca.text = producto.nomMarca
        holder.txtCategoria.text = when (producto.nomCategoria) {
            "Smartphones y Accesorios" -> "Smartphones"
            "Laptops y Equipos de Cómputo"-> "Laptops"
            "Audio y Accesorios Bluetooth"-> "Audio"
            "Gaming y Consolas"-> "Gaming"
            "Hogar Inteligente y Domótica"-> "Hogar"
            "Cámaras y Fotografía Digital"-> "Cámaras"
            else -> "Marca"
        }
        holder.txtPrecio.text = "S/. %.2f".format(producto.preUni)
        holder.txtCantidad.text = producto.cantidad.toString()

        cambiarBackgroundTint(holder.txtMarca)
        cambiarBackgroundTint(holder.txtCategoria)

        val urlImagen = "http://10.0.2.2:5010/imagenes/productos/${producto.imgProducto}"
        Glide.with(holder.itemView.context)
            .load(urlImagen)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.imagen)

        holder.btnMas.setOnClickListener {
            val currentQuantity = producto.cantidad
            val newQuantity = currentQuantity + 1
            lista[position].cantidad = newQuantity
            onQuantityChangeListener?.invoke(position, newQuantity)
            holder.txtCantidad.text = newQuantity.toString()
        }

        holder.btnMenos.setOnClickListener {
            val currentQuantity = producto.cantidad
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                lista[position].cantidad = newQuantity
                onQuantityChangeListener?.invoke(position, newQuantity)
                holder.txtCantidad.text = newQuantity.toString()
            }
        }
    }
}