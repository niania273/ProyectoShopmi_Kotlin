package com.proyecto.proyectoshopmi.data.model.request

data class DetallePedidoRequest(
    var codProducto: Int,
    var preUni: Double,
    var cantidad: Int
)