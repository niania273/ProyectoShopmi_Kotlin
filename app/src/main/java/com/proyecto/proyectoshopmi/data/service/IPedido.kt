package com.proyecto.proyectoshopmi.data.service

import com.proyecto.proyectoshopmi.data.model.request.PedidoDetalleRequest
import com.proyecto.proyectoshopmi.data.model.response.PedidoDetalleResponse
import com.proyecto.proyectoshopmi.data.model.response.PedidoResponse
import com.proyecto.proyectoshopmi.data.model.response.GeneralResponse
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
    fun obtenerPedido(@Path("codPedido") codPedido: Int): Call<PedidoDetalleResponse>

    @POST("pedidos/registrarPedido")
    fun registrarPedido(@Body pedidoDetalleRequest: PedidoDetalleRequest): Call<GeneralResponse>

    @PUT("pedidos/actualizarPedido/{codPedido}")
    fun actualizarPedido(@Path("codPedido") codPedido: Int, @Body codEstado: Int): Call<GeneralResponse>

    @DELETE("pedidos/eliminarPedido/{codPedido}")
    fun eliminarPedido(@Path("codPedido") codPedido: Int): Call<GeneralResponse>

}