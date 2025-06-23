package com.proyecto.proyectoshopmi.data.service

import android.util.Log
import com.proyecto.proyectoshopmi.data.client.RetrofitClient
import com.proyecto.proyectoshopmi.data.model.response.GeneralResponse
import com.proyecto.proyectoshopmi.data.model.response.ProductoResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        productoData: Map<String, RequestBody>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val imagePart = imageFile?.let {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            MultipartBody.Part.createFormData("imgProducto", it.name, requestFile)
        }

        api.registrarProducto(imagePart, productoData).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    val generalResponse = response.body()

                    if (generalResponse != null) {
                        Log.d("ProductoService", "Respuesta de registro: ${generalResponse.mensaje}")
                        onSuccess(generalResponse.mensaje)
                    } else {
                        Log.e("ProductoService", "Cuerpo de respuesta nulo para un registro exitoso HTTP.")
                        onError("Respuesta del servidor vacía o inesperada.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductoService", "Error HTTP ${response.code()}: ${errorBody ?: response.message()}")
                    onError("Error ${response.code()}: ${errorBody ?: response.message()}")
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                Log.e("ProductoService", "Fallo de conexión: ${t.localizedMessage}", t)
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
