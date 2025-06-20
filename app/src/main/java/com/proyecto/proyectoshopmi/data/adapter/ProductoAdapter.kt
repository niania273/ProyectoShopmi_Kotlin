package com.proyecto.proyectoshopmi.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout // Importar LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.model.response.ProductoResponse

class ProductoAdapter(
    private var productos: List<ProductoResponse>,
    private val onItemClicked: (ProductoResponse) -> Unit,
    private val onActualizarClicked: ((ProductoResponse) -> Unit)? = null,
    private val onDesactivarClicked: ((ProductoResponse) -> Unit)? = null
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto, onItemClicked, onActualizarClicked, onDesactivarClicked)
    }

    override fun getItemCount(): Int = productos.size

    // Función para actualizar los datos del adaptador
    fun updateData(newProducts: List<ProductoResponse>) {
        productos = newProducts
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val estado: TextView = itemView.findViewById(R.id.tvEstado)
        val nombreMarca: TextView = itemView.findViewById(R.id.tvMarca)
        private val nombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        private val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val imagen: ImageView = itemView.findViewById(R.id.ivProducto)
        private val btnAgregarCarrito: View = itemView.findViewById(R.id.btnAgregarCarrito)
        private val btnActualizar: View = itemView.findViewById(R.id.btnActualizar)
        private val btnDesactivar: View = itemView.findViewById(R.id.btnDesactivar)
        private val layoutBotonesAdicionales: LinearLayout = itemView.findViewById(R.id.layoutBotonesAdicionales)


        fun bind(
            producto: ProductoResponse,
            onItemClicked: (ProductoResponse) -> Unit,
            onActualizarClicked: ((ProductoResponse) -> Unit)?,
            onDesactivarClicked: ((ProductoResponse) -> Unit)?
        ) {
            nombreMarca.text = producto.nombreMarca.uppercase()
            nombre.text = producto.nomProducto
            descripcion.text = producto.descripcion
            precio.text = "S/. ${"%.2f".format(producto.preUni)}"

            if (producto.estProducto == true) {
                estado.text = "Disponible"
                estado.visibility = View.VISIBLE
            } else {
                estado.visibility = View.GONE
            }

            val urlImagen = "http://10.0.2.2:5010/imagenes/productos/${producto.imgProducto}"
            Glide.with(itemView.context)
                .load(urlImagen)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(imagen)

            btnAgregarCarrito.setOnClickListener {
                onItemClicked(producto)
            }

            // Manejo de botones adicionales (actualizar/desactivar)
            // Si quieres que estos botones aparezcan bajo ciertas condiciones, aquí es donde lo harías.
            // Por ejemplo, si solo deben aparecer para el administrador:
            // if (esAdmin) {
            //    layoutBotonesAdicionales.visibility = View.VISIBLE
            //    btnActualizar.setOnClickListener { onActualizarClicked?.invoke(producto) }
            //    btnDesactivar.setOnClickListener { onDesactivarClicked?.invoke(producto) }
            // } else {
            //    layoutBotonesAdicionales.visibility = View.GONE
            // }
            // Para el propósito de esta imagen, los dejaremos ocultos por defecto en el XML.
        }
    }
}