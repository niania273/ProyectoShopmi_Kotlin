package com.proyecto.proyectoshopmi.data.service

import android.content.Context
import com.proyecto.proyectoshopmi.data.client.RetrofitClient
import com.proyecto.proyectoshopmi.data.model.request.UsuarioRequestLogin
import com.proyecto.proyectoshopmi.data.model.request.UsuarioRequestRegistro
import com.proyecto.proyectoshopmi.data.model.response.LoginResponse
import com.proyecto.proyectoshopmi.data.model.response.GeneralResponse
import com.proyecto.proyectoshopmi.helper.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuarioService(private val context: Context){

    private val api = RetrofitClient.usuarioService
    private val sessionManager = SessionManager(context)

    fun registrarUsuario(
        usuarioRequestRegistro: UsuarioRequestRegistro,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        api.register(usuarioRequestRegistro).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    val mensaje = response.body()?.mensaje ?: "Registro exitoso, pero sin mensaje del servidor."
                    onSuccess(mensaje)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

    fun iniciarSesionUsuario(
        usuarioRequestLogin: UsuarioRequestLogin,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        api.login(usuarioRequestLogin).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        sessionManager.guardarToken(loginResponse.token)
                        sessionManager.guardarUsuario(loginResponse.usuario)
                        onSuccess(loginResponse)
                    } else {
                        onError("Respuesta vacía del servidor.")
                    }
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError("Fallo de conexión: ${t.localizedMessage}")
            }
        })
    }

}