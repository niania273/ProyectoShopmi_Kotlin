package com.proyecto.proyectoshopmi.data.model.response

data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponse
)

data class UsuarioResponse(
    val id: Int,
    val numeroDocumento: String,
    val apellido: String,
    val nombre: String,
    val contrasenia: String?,
    val fechaNacimiento: String,
    val sexo: String,
    val telefono: String,
    val fechaCreacion: String,
    val estado: Boolean,
    val rolId: Int,
    val userName: String?,
    val normalizedUserName: String?,
    val email: String,
    val normalizedEmail: String?,
    val emailConfirmed: Boolean,
    val passwordHash: String,
    val securityStamp: String?,
    val concurrencyStamp: String,
    val phoneNumber: String?,
    val phoneNumberConfirmed: Boolean,
    val twoFactorEnabled: Boolean,
    val lockoutEnd: String?,
    val lockoutEnabled: Boolean,
    val accessFailedCount: Int
)