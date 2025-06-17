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

class ProductoAdapter(
    private val productos: List<ProductoResponse>,
    private val onItemClicked: (ProductoResponse) -> Unit,
    //private val onActualizar: (ProductoResponse) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)

        val screenWidth = parent.context.resources.displayMetrics.widthPixels
        view.layoutParams.width = (screenWidth * 0.5).toInt()

        // Opcional: agregar margen entre items programáticamente
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(16, 16, 16, 16)
        view.layoutParams = layoutParams

        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto, onItemClicked) {}
    }

    override fun getItemCount(): Int = productos.size

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val estado: TextView = itemView.findViewById(R.id.tvEstado)
        private val nombreMarca: TextView = itemView.findViewById(R.id.tvMarca)
        private val nombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        private val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val imagen: ImageView = itemView.findViewById(R.id.ivProducto)
        private val btnAgregarCarrito: View = itemView.findViewById(R.id.btnAgregarCarrito)
        private val btnActualizar: View = itemView.findViewById(R.id.btnActualizar)
        private val btnDesactivar: View = itemView.findViewById(R.id.btnDesactivar)

        fun bind(
            producto: ProductoResponse,
            onItemClicked: (ProductoResponse) -> Unit,
            function: () -> Unit,
           // onActualizar: (ProductoResponse) -> Unit
        ) {
            nombre.text = producto.nomProducto
            estado.text = producto.estProducto.toString()
            descripcion.text = producto.descripcion
            nombreMarca.text = producto.nombreMarca
            precio.text = "S/. ${"%.2f".format(producto.preUni)}"

            val urlImagen = "http://10.0.2.2:5010/imagenes/productos/${producto.imgProducto}"
            Glide.with(itemView.context)
                .load(urlImagen)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(imagen)

            // Clicks individuales si los quieres manejar
            btnAgregarCarrito.setOnClickListener {
                onItemClicked(producto)
            }
            btnActualizar.setOnClickListener {
                /*onActualizar(producto)*/
            }
            btnDesactivar.setOnClickListener {
                // Acción: desactivar
            }
        }
    }
}