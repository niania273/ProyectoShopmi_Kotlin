package com.proyecto.proyectoshopmi.data.client

import com.proyecto.proyectoshopmi.data.service.ICategoria
import com.proyecto.proyectoshopmi.data.service.IEstado
import com.proyecto.proyectoshopmi.data.service.IMarca
import com.proyecto.proyectoshopmi.data.service.IPedido
import com.proyecto.proyectoshopmi.data.service.IProducto
import com.proyecto.proyectoshopmi.data.service.IUsuario
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient{
    private const val BASE_URL = "http://10.0.2.2:5010/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Crea un OkHttpClient con el interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // 3. Pasa este OkHttpClient al constructor de Retrofit
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    //Autenticacion
    val usuarioService : IUsuario by lazy{
        retrofit.create(IUsuario::class.java)
    }

    //Categoria
    val categoriaService: ICategoria by lazy {
        retrofit.create(ICategoria::class.java)
    }

    //Marca
    val marcaService: IMarca by lazy {
        retrofit.create(IMarca::class.java)
    }

    //Producto
    val productoService: IProducto by lazy {
        retrofit.create(IProducto::class.java)
    }

    //Estado Pedido
    val estadoPedidoService: IEstado by lazy {
        retrofit.create(IEstado::class.java)
    }

    //Pedido
    val pedidoService: IPedido by lazy {
        retrofit.create(IPedido::class.java)
    }
}