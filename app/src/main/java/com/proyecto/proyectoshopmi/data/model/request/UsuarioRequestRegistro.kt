package com.proyecto.proyectoshopmi.data.model.request

data class UsuarioRequestRegistro(
    val numeroDocumento: Int,
    val apeUsuario: String,
    val nomUsuario: String,
    val fecNac: String,
    val sexUsuario: Char,
    val corUsuario: String,
    val conUsuario: String,
    val fecCre: String,
    val estUsuario: Char,
    val codRol: Int
)