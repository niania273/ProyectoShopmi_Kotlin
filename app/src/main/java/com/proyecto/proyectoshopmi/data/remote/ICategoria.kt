package com.proyecto.proyectoshopmi.data.remote

import retrofit2.Call
import com.proyecto.proyectoshopmi.data.model.SelectResponse
import retrofit2.http.GET

interface ICategoria {

    @GET("/categorias/SelectCategorias")
    fun getCategoriasSelect(): Call<List<SelectResponse>>
}