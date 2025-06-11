package com.proyecto.proyectoshopmi.data.remote

import retrofit2.Retrofit

object RetrofitClient{
    private const val BASE_URL = ""

    val retrofit: Retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
    }

    val usuarioService : IUsuario by lazy{
        retrofit.create(IUsuario::class.java)
    }
}