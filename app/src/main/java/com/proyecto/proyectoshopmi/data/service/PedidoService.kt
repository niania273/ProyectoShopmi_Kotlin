package com.proyecto.proyectoshopmi.data.service

import android.content.Context
import com.proyecto.proyectoshopmi.data.client.RetrofitClient
import com.proyecto.proyectoshopmi.data.model.request.PedidoDetalleRequest
import com.proyecto.proyectoshopmi.data.model.response.PedidoResponse
import com.proyecto.proyectoshopmi.data.model.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PedidoService(private val context: Context) {

    private val api = RetrofitClient.pedidoService

    fun listarPedidos(
        onSuccess: (List<PedidoResponse>) -> Unit,
        onError: (String) -> Unit
    ){
        api.listarPedidos().enqueue(object: Callback<List<PedidoResponse>>{
            override fun onResponse(
                call: Call<List<PedidoResponse>>,
                response: Response<List<PedidoResponse>>)
                {
                if (response.isSuccessful) {
                    val pedidos = response.body() ?: emptyList()
                    onSuccess(pedidos)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<PedidoResponse>>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

    fun registrarPedido(
        pedidoRequest: PedidoDetalleRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ){
        api.registrarPedido(pedidoRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val mensaje = response.body()?.mensaje ?: "Registro exitoso, pero sin mensaje del servidor."
                    onSuccess(mensaje)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }
}