package com.proyecto.proyectoshopmi.data.remote

import com.proyecto.proyectoshopmi.data.model.UsuarioRequestLogin
import com.proyecto.proyectoshopmi.data.model.UsuarioRequestRegistro
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IUsuario {

    @POST("/inicio/registrarse")
    suspend fun Register(@Body usuarioRequestRegistro: UsuarioRequestRegistro): Response<String>

    @POST("inicio/iniciarsesion")
    suspend fun Login(@Body usuarioRequestLogin: UsuarioRequestLogin): Response<String>
}