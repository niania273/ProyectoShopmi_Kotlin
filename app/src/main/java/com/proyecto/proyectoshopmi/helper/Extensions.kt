package com.proyecto.proyectoshopmi.helper

import com.proyecto.proyectoshopmi.data.model.request.ProductoFormRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun ProductoFormRequest.toMultipartParts(): Map<String, RequestBody> {
    val map = mutableMapOf<String, RequestBody>()
    map["codProducto"] = codProducto.toString().toRequestBody("text/plain".toMediaType())
    map["nomProducto"] = nomProducto.toRequestBody("text/plain".toMediaType())
    map["descripcion"] = descripcion.toRequestBody("text/plain".toMediaType())
    map["preUni"] = preUni.toString().toRequestBody("text/plain".toMediaType())
    map["stock"] = stock.toString().toRequestBody("text/plain".toMediaType())
    map["estProducto"] = estProducto.toString().toRequestBody("text/plain".toMediaType())
    map["codCategoria"] = codCategoria.toString().toRequestBody("text/plain".toMediaType())
    map["codMarca"] = codMarca.toString().toRequestBody("text/plain".toMediaType())
    imgActual?.let {
        map["imgActual"] = it.toRequestBody("text/plain".toMediaType())
    }
    return map
}

fun File.toImagePart(partName: String): MultipartBody.Part {
    val requestFile = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, name, requestFile)
}