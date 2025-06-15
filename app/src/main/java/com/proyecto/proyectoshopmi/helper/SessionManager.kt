package com.proyecto.proyectoshopmi.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.proyecto.proyectoshopmi.data.model.response.UsuarioResponse

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun guardarToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun obtenerToken(): String? {
        return prefs.getString("token", null)
    }

    fun guardarUsuario(usuario: UsuarioResponse) {
        val usuarioJson = Gson().toJson(usuario)
        prefs.edit().putString("usuario", usuarioJson).apply()
    }

    fun obtenerUsuario(): UsuarioResponse? {
        val usuarioJson = prefs.getString("usuario", null)
        return if (usuarioJson != null) Gson().fromJson(usuarioJson, UsuarioResponse::class.java) else null
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}