package com.proyecto.proyectoshopmi.data.service

import retrofit2.Call
import com.proyecto.proyectoshopmi.data.model.SelectResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface IMarca {

    @GET("marcas/SelectMarcas")
    fun selectMarcas(): Call<List<SelectResponse>>

    @GET("marcas/BuscarMarca/{codMarca}")
    fun buscarMarca(@Path("codMarca") codMarca: Int): Call<List<SelectResponse>>
}