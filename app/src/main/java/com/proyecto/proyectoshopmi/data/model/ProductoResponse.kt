package com.proyecto.proyectoshopmi.data.model

data class ProductoResponse (
    val codProducto: Int,
    val imgProducto: String,
    val nomProducto: String,
    val descripcion: String,
    val preUni: Double,
    val stock: Int,
    val estProducto: Boolean,
    val nomCategoria: String,
    val nomMarca: String
)