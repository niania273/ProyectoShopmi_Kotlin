package com.proyecto.proyectoshopmi.data.service

import com.proyecto.proyectoshopmi.data.model.response.PedidoDetalleResponse
import com.proyecto.proyectoshopmi.data.model.response.PedidoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IPedido {

    @GET("pedidos/listarPedidos")
    fun listarPedidos(): Call<List<PedidoResponse>>

    @GET("pedidos/obtenerPedido/{codPedido}")
    fun obtenerPedido(@Path("codPedido") codPedido: Int): PedidoDetalleResponse

    @POST("pedidos/registrarPedido")
    fun registrarPedido(@Body pedidoDetalleResponse: PedidoDetalleResponse): Call<String>

    @PUT("pedidos/actualizarPedido/{codPedido}")
    fun actualizarPedido(@Path("codPedido") codPedido: Int, @Body codEstado: Int): Call<String>

    @DELETE("pedidos/eliminarPedido/{codPedido}")
    fun eliminarPedido(@Path("codPedido") codPedido: Int): Call<String>

}