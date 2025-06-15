package com.proyecto.proyectoshopmi.data.model.request

data class DetallePedidoRequest(
    val codProducto: Int,
    val preUni: Double,
    val cantidad: Int
)