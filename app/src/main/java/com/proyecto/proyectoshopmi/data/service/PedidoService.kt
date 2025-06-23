package com.proyecto.proyectoshopmi.data.service

import android.content.Context
import com.proyecto.proyectoshopmi.data.client.RetrofitClient
import com.proyecto.proyectoshopmi.data.model.request.PedidoDetalleRequest
import com.proyecto.proyectoshopmi.data.model.response.PedidoResponse
import com.proyecto.proyectoshopmi.data.model.response.GeneralResponse
import com.proyecto.proyectoshopmi.data.model.response.PedidoDetalleResponse
import com.proyecto.proyectoshopmi.data.model.response.PedidoOneResponse
import com.proyecto.proyectoshopmi.data.model.response.ProductoPedidoResponse
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

    fun verPedido(
        codPedido: Int,
        onSuccess: (PedidoDetalleResponse) -> Unit,
        onError: (String) -> Unit
    ){
        api.obtenerPedido(codPedido).enqueue(object: Callback<PedidoDetalleResponse>{
            override fun onResponse(
                call: Call<PedidoDetalleResponse>,
                response: Response<PedidoDetalleResponse>)
            {
                if (response.isSuccessful) {
                    val pedido = response.body() ?: PedidoDetalleResponse(PedidoOneResponse(0,"","",0.0,0, false), emptyList<ProductoPedidoResponse>())
                    onSuccess(pedido)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<PedidoDetalleResponse>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

    fun registrarPedido(
        pedidoRequest: PedidoDetalleRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ){
        api.registrarPedido(pedidoRequest).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    val mensaje = response.body()?.mensaje ?: "Registro exitoso, pero sin mensaje del servidor."
                    onSuccess(mensaje)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

    fun actualizarPedido(
        codPedido: Int,
        codEstado: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ){
        api.actualizarPedido(codPedido, codEstado).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    val mensaje = response.body()?.mensaje ?: "Actualización exitosa, pero sin mensaje del servidor."
                    onSuccess(mensaje)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

    fun eliminarPedido(
        codPedido: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ){
        api.eliminarPedido(codPedido).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    val mensaje = response.body()?.mensaje ?: "Actualización exitosa, pero sin mensaje del servidor."
                    onSuccess(mensaje)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }
}