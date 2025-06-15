package com.proyecto.proyectoshopmi.data.service

import com.proyecto.proyectoshopmi.data.model.response.SelectResponse
import retrofit2.Call
import retrofit2.http.GET

interface IEstado {

    @GET("estados/estado/SelectEstados")
    fun selectEstados(): Call<List<SelectResponse>>
}