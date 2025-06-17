package com.proyecto.proyectoshopmi.data.model.request

data class ProductoDetalleRequest(
    val codProducto: Int,
    val nomProducto: String,
    val imgProducto: String? = null,
    val preUni: Double,
    val stock: Int,
    val cantidad: Int
)