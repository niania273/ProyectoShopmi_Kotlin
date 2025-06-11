package com.proyecto.proyectoshopmi.data.remote

import com.proyecto.proyectoshopmi.data.model.PedidoDetalleResponse
import com.proyecto.proyectoshopmi.data.model.PedidoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IPedido {

    @GET("/pedidos/listarPedidos")
    suspend fun listarPedidos(): Call<List<PedidoResponse>>

    @GET("/pedidos/obtenerPedido/{codPedido}")
    suspend fun obtenerPedido(@Path("codPedido") codPedido: Int): PedidoDetalleResponse

    @POST("/pedidos/registrarPedido")
    suspend fun registrarPedido(@Body pedidoDetalleResponse: PedidoDetalleResponse): Call<String>

    @PUT("/pedidos/actualizarPedido/{codPedido}")
    suspend fun actualizarPedido(@Path("codPedido") codPedido: Int, @Body codEstado: Int): Call<String>

    @DELETE("pedidos/eliminarPedido/{codPedido}")
    suspend fun eliminarPedido(@Path("codPedido") codPedido: Int): Call<String>

}