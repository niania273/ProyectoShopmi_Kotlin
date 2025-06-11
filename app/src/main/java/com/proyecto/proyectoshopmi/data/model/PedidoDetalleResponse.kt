package com.proyecto.proyectoshopmi.data.model

data class PedidoDetalleResponse (
    val pedido: PedidoOneResponse,
    var productos: List<ProductoPedidoResponse>
)