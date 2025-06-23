package com.proyecto.proyectoshopmi.data.service

import com.proyecto.proyectoshopmi.data.model.response.GeneralResponse
import com.proyecto.proyectoshopmi.data.model.response.ProductoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface IProducto {

    @GET("productos/ListarPorCategoria/{codCategoria}")
    fun listarPorCategoria(@Path("codCategoria") codCategoria: Int): Call<List<ProductoResponse>>

    @GET("productos/ListarTop5ProductosMasBaratos")
    fun listarTop5MasBaratos(): Call<List<ProductoResponse>>

    @GET("productos/ObtenerProducto/{codProducto}")
    fun obtenerProducto(@Path("codProducto") codProducto: Int): Call<ProductoResponse>

    @Multipart
    @POST("productos/RegistrarProducto")
    fun registrarProducto(@Part image: MultipartBody.Part?,
                          @PartMap datos: Map<String, @JvmSuppressWildcards RequestBody>): Call<GeneralResponse>

    @Multipart
    @PUT("productos/ActualizarProducto")
    fun actualizarProducto(@Part image: MultipartBody.Part?,
                           @PartMap datos: Map<String, @JvmSuppressWildcards RequestBody>): Call<GeneralResponse>


    @PUT("productos/CambiarEstadoProducto/{codProducto}")
    fun cambiarEstadoProducto(@Path("codProducto") codProducto: Int): Call<String>
}