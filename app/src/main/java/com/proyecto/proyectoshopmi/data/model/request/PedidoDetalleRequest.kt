package com.proyecto.proyectoshopmi.data.model.request

data class PedidoDetalleRequest(
    var codUsuario: Int,
    var fecPed: String,
    var precioTotal: Double,
    var DetallePedido: List<DetallePedidoRequest>
)
