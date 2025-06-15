package com.proyecto.proyectoshopmi.data.model.response

data class PedidoResponse (
    val codPedido: Int,
    val fecPedido: String,
    val cliente: String,
    val precioTotal: Double,
    val nomEstado: String,
    val cantidadTotal: Int,
    val estPed: Boolean
)