package com.proyecto.proyectoshopmi.data.service

import com.proyecto.proyectoshopmi.data.model.request.UsuarioRequestLogin
import com.proyecto.proyectoshopmi.data.model.request.UsuarioRequestRegistro
import com.proyecto.proyectoshopmi.data.model.response.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IUsuario {

    @POST("inicio/registrarse")
    fun register(@Body request: UsuarioRequestRegistro): Call<String>

    @POST("inicio/iniciarsesion")
    fun login(@Body request: UsuarioRequestLogin): Call<LoginResponse>
}