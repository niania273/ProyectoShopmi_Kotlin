package com.proyecto.proyectoshopmi.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.proyecto.proyectoshopmi.data.model.response.UsuarioResponse
import androidx.core.content.edit
import com.proyecto.proyectoshopmi.data.model.response.ProductoCarritoResponse

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun guardarToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun obtenerToken(): String? {
        return prefs.getString("token", null)
    }

    fun guardarUsuario(usuario: UsuarioResponse) {
        val usuarioJson = Gson().toJson(usuario)
        prefs.edit().putString("usuario", usuarioJson).apply()
    }

    fun obtenerUsuario(): UsuarioResponse? {
        val usuarioJson = prefs.getString("usuario", null)
        return if (usuarioJson != null) Gson().fromJson(usuarioJson, UsuarioResponse::class.java) else null
    }

    fun cerrarSesion() {
        prefs.edit {
            clear()
        }
    }

    fun guardarCarrito(productos: List<ProductoCarritoResponse>) {
        val carritoJson = Gson().toJson(productos)
        prefs.edit().putString("cartItemList", carritoJson).apply()
    }

    fun obtenerCarrito(): MutableList<ProductoCarritoResponse> {
        val carritoJson = prefs.getString("cartItemList", null)
        return if (!carritoJson.isNullOrEmpty()) {
            val type = object : com.google.gson.reflect.TypeToken<List<ProductoCarritoResponse>>() {}.type
            Gson().fromJson(carritoJson, type)
        } else {
            mutableListOf()
        }
    }

    fun agregarProductoAlCarrito(producto: ProductoCarritoResponse) {
        val productos = obtenerCarrito().toMutableList()
        val index = productos.indexOfFirst { it.codProducto == producto.codProducto }

        if (index != -1) {
            val actualizado = productos[index].copy(cantidad = productos[index].cantidad + 1)
            productos[index] = actualizado
        } else {
            productos.add(producto.copy(cantidad = 1))
        }

        guardarCarrito(productos)
    }

    fun eliminarProductoDelCarrito(codProducto: Int) {
        val productos = obtenerCarrito().filter { it.codProducto != codProducto }
        guardarCarrito(productos)
    }

    fun actualizarCantidadProducto(codProducto: Int, cantidad: Int) {
        val productos = obtenerCarrito().map {
            if (it.codProducto == codProducto) it.copy(cantidad = cantidad) else it
        }
        guardarCarrito(productos)
    }

    fun vaciarCarrito() {
        prefs.edit().remove("cartItemList").apply()
    }
}