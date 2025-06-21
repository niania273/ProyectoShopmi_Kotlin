package com.proyecto.proyectoshopmi.data.model.response

data class ProductoCarritoResponse(
    val codProducto: Int,
    val nomProducto: String,
    val imgProducto: String? = null,
    val preUni: Double,
    val stock: Int,
    var cantidad: Int,
    val nomMarca: String,
    val nomCategoria: String
)