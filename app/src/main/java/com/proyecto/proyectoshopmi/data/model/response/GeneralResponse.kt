package com.proyecto.proyectoshopmi.data.model.response

import com.google.gson.annotations.SerializedName

data class GeneralResponse(
    @SerializedName("mensaje")
    val mensaje: String
)
