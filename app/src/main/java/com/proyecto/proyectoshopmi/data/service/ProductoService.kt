package com.proyecto.proyectoshopmi.data.service

import com.proyecto.proyectoshopmi.data.client.RetrofitClient
import com.proyecto.proyectoshopmi.data.model.response.ProductoResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.io.File

class ProductoService {

    private val api = RetrofitClient.productoService

    // Top 5 productos más baratos
    fun obtenerTop5MasBaratos(
        onSuccess: (List<ProductoResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.listarTop5MasBaratos().enqueue(object : Callback<List<ProductoResponse>> {
            override fun onResponse(
                call: Call<List<ProductoResponse>>,
                response: Response<List<ProductoResponse>>
            ) {

                if (response.isSuccessful) {
                    val productos = response.body() ?: emptyList()
                    onSuccess(productos)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ProductoResponse>>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

    //Productos por categoría
    fun obtenerProductosPorCategoria(
        codCategoria: Int,
        onSuccess: (List<ProductoResponse>) -> Unit,
        onError: (String) -> Unit
    ){
        api.listarPorCategoria(codCategoria).enqueue(object : Callback<List<ProductoResponse>> {
            override fun onResponse(
                call: Call<List<ProductoResponse>>,
                response: Response<List<ProductoResponse>>
            ) {
                if (response.isSuccessful) {
                    val productos = response.body() ?: emptyList()
                    onSuccess(productos)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ProductoResponse>>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

    //Registrar producto
    fun registrarProducto(
        imageFile: File?,
        productoData: MutableMap<String, RequestBody>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val imagePart = imageFile?.let {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            MultipartBody.Part.createFormData("imgProducto", it.name, requestFile)
        }

        val dataMap: Map<String, RequestBody> = productoData.mapValues {
            RequestBody.create("text/plain".toMediaTypeOrNull(), it.value.toString())
        }

        api.registrarProducto(imagePart, dataMap).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: "Producto registrado con éxito")
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

    fun cambiarEstadoProducto(
        codProducto: Int
    ){
        api.cambiarEstadoProducto(codProducto)
    }

}
