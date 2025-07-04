package com.proyecto.proyectoshopmi.data.service

import retrofit2.Call
import com.proyecto.proyectoshopmi.data.model.response.SelectResponse
import retrofit2.http.GET

interface ICategoria {

    @GET("categorias/SelectCategorias")
    fun selectCategorias(): Call<List<SelectResponse>>
}