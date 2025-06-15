package com.proyecto.proyectoshopmi.data.model.request

data class ProductoRequest(
    var codProducto: Int,
    var imgProducto: String?,
    var nomProducto: String?,
    var descripcion: String?,
    var preUni: Double,
    var stock: Int,
    var estProducto: Boolean,
    var codCategoria: Int,
    var codMarca: Int
)