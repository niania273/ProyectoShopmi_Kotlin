package com.proyecto.proyectoshopmi.data.service

import com.proyecto.proyectoshopmi.data.client.RetrofitClient
import com.proyecto.proyectoshopmi.data.model.response.SelectResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EstadoService {

    private val api = RetrofitClient.estadoPedidoService

    //SelectEstados
    fun selectEstados(
        onSuccess: (List<SelectResponse>) -> Unit,
        onError: (String) -> Unit
    ){
        api.selectEstados().enqueue(object : Callback<List<SelectResponse>> {
            override fun onResponse(
                call: Call<List<SelectResponse>>,
                response: Response<List<SelectResponse>>
            ) {

                if (response.isSuccessful) {
                    val categorias = response.body() ?: emptyList()
                    onSuccess(categorias)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<SelectResponse>>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }
}