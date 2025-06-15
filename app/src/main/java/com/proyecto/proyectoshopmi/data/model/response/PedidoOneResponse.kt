package com.proyecto.proyectoshopmi.data.model.response

data class PedidoOneResponse (
    val codPedido: Int,
    val cliente: String,
    val fecPed: String,
    val precioTotal: Double,
    val codEstado: Int,
    val estped: Boolean
)