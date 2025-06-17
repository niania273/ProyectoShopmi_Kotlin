package com.proyecto.proyectoshopmi.data.model.request

data class UsuarioRequestRegistro(
    val numeroDocumento: String,
    val apellido: String,
    val nombre: String,
    val fechaNacimiento: String,
    val sexo: String,
    val telefono: String,
    val correo: String,
    val contrasenia: String
)