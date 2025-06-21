package com.proyecto.proyectoshopmi.data.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.model.response.ProductoResponse
import com.proyecto.proyectoshopmi.helper.SessionManager
import androidx.fragment.app.FragmentActivity

class ProductoAdapter(
    private var productos: List<ProductoResponse>,
    private val onAddToCartClicked: (ProductoResponse) -> Unit,
    private val onViewProductClicked: ((ProductoResponse) -> Unit),
    private val onActualizarClicked: ((ProductoResponse) -> Unit)? = null,
    private val onDesactivarClicked: ((ProductoResponse) -> Unit)? = null
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)

        val activity = parent.context as? FragmentActivity
        return ProductoViewHolder(view, activity)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto, onAddToCartClicked, onViewProductClicked, onActualizarClicked, onDesactivarClicked)
    }

    override fun getItemCount(): Int = productos.size

    fun updateData(newProducts: List<ProductoResponse>) {
        productos = newProducts
        notifyDataSetChanged()
    }

    class ProductoViewHolder(
        itemView: View,
        private val activity: FragmentActivity?
    ) : RecyclerView.ViewHolder(itemView) {

        private val sessionManager = SessionManager(itemView.context)
        private val rolId = sessionManager.obtenerUsuario()?.rolId

        private val estado: TextView = itemView.findViewById(R.id.tvEstado)
        private val nombreMarca: TextView = itemView.findViewById(R.id.tvMarca)
        private val nombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        private val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val imagen: ImageView = itemView.findViewById(R.id.ivProducto)
        private val llAgregarCarrito: LinearLayout = itemView.findViewById(R.id.llAgregarCarrito)
        private val btnAgregarCarrito: MaterialButton = itemView.findViewById(R.id.btnAgregarCarrito)
        private val btnActualizar: View = itemView.findViewById(R.id.item_btnActualizar)
        private val btnEliminar: View = itemView.findViewById(R.id.item_btnEliminar)
        private val layoutBotonesAdicionales: LinearLayout = itemView.findViewById(R.id.linearLayoutButtons)

        fun bind(
            producto: ProductoResponse,
            onAddToCartClicked: (ProductoResponse) -> Unit,
            onViewProductClicked: (ProductoResponse) -> Unit,
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
                estado.text = "Agotado"
                estado.setBackgroundColor(Color.RED)
                estado.visibility = View.GONE
            }

            val urlImagen = "http://10.0.2.2:5010/imagenes/productos/${producto.imgProducto}"
            Glide.with(itemView.context)
                .load(urlImagen)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(imagen)

            imagen.setOnClickListener {
                onViewProductClicked(producto)
            }

            when (rolId) {
                1 -> {
                    llAgregarCarrito.visibility = View.VISIBLE
                    btnAgregarCarrito.setOnClickListener {
                        onAddToCartClicked(producto)
                    }
                }
                2, 3 -> {
                    layoutBotonesAdicionales.visibility = View.VISIBLE
                    btnActualizar.setOnClickListener {
                        onActualizarClicked?.invoke(producto)
                    }
                    btnEliminar.setOnClickListener {
                        onDesactivarClicked?.invoke(producto)
                    }
                }
                else -> {
                    llAgregarCarrito.visibility = View.GONE
                    layoutBotonesAdicionales.visibility = View.GONE
                }
            }
        }
    }
}