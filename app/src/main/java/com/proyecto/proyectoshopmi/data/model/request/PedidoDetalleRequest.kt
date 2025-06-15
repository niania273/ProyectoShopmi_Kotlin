package com.proyecto.proyectoshopmi.data.model.request

import java.util.Date

data class PedidoDetalleRequest(
    var codUsuario: Int,
    var fecPed: Date,
    var precioTotal: Double,
    var codEstado: Int,
    var estPed: Boolean,
    var DetallePedido: List<DetallePedidoRequest>
)
