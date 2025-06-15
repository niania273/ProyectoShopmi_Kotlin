package com.proyecto.proyectoshopmi.data.model.request

data class ProductoFormRequest(
    val codProducto: Int,
    val nomProducto: String,
    val descripcion: String,
    val preUni: Double,
    val stock: Int,
    val estProducto: Boolean,
    val codCategoria: Int,
    val codMarca: Int,
    val imgActual: String?
)