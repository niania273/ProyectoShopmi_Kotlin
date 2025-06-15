package com.proyecto.proyectoshopmi.data.model.response

data class PedidoDetalleResponse (
    val pedido: PedidoOneResponse,
    var productos: List<ProductoPedidoResponse>
)