package com.proyecto.proyectoshopmi.data.remote

import com.proyecto.proyectoshopmi.data.model.SelectResponse
import retrofit2.Call
import retrofit2.http.GET

interface IEstado {

    @GET("estados/estado/SelectEstados")
    fun selectEstados(): Call<List<SelectResponse>>
}