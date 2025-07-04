package com.proyecto.proyectoshopmi.fragment.producto

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log // Import for Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast // For showing a toast if data is missing
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import cn.pedant.SweetAlert.SweetAlertDialog
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.model.response.SelectResponse
import com.proyecto.proyectoshopmi.data.service.CategoriaService
import com.proyecto.proyectoshopmi.data.service.MarcaService
import com.proyecto.proyectoshopmi.data.service.ProductoService
import com.proyecto.proyectoshopmi.databinding.FragmentActualizarProductoBinding
import com.proyecto.proyectoshopmi.fragment.HomeFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import com.bumptech.glide.Glide

// No need for ProductoResponse import here if not receiving the object

class ActualizarProductoFragment : Fragment() {

    private var _binding: FragmentActualizarProductoBinding? = null
    private val binding get() = _binding!!

    private val productoService = ProductoService()
    private val marcaService = MarcaService()
    private val categoriaService = CategoriaService()

    private var selectedImageFile: File? = null
    private var selectedCategoriaId: Int? = null
    private var selectedMarcaId: Int? = null



    // Properties to hold the received product data
    private var codProducto: Int = 0
    private var nomProducto: String = ""
    private var imgProducto: String = ""
    private var preUni: Double = 0.0
    private var stock: Int = 0
    private var descripcion: String = ""
    private var estProducto: Boolean = false
    private var codCategoria: String = ""
    private var codMarca:String = ""


    // Launcher para seleccionar imagen de la galería
    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.tvArchivoSeleccionado.text = getFileNameFromUri(requireContext(), it)
                selectedImageFile = uriToFile(requireContext(), it)

            }
        }

    // Listas para almacenar categorías y marcas
    private var categorias: List<SelectResponse> = emptyList()
    private var marcas: List<SelectResponse> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            codProducto = it.getInt("codProducto", 0)
            nomProducto = it.getString("nomProducto", "") ?: ""
            imgProducto = it.getString("imgProducto", "") ?: ""
            preUni = it.getDouble("preUni", 0.0)
            stock = it.getInt("stock", 0)
            descripcion = it.getString("descripcion", "") ?: ""
            estProducto = it.getBoolean("estProducto", false)
            codCategoria = it.getString("nomCategoria", "") ?: ""
            codMarca = it.getString("nombreMarca", "") ?: ""
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActualizarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Precargar los datos del producto
        binding.edtCodigo.setText(codProducto.toString()) // Campo de código (codProducto)
        binding.edtNombreProducto.setText(nomProducto) // Campo de nombre (nomProducto)
        binding.edtPrecio.setText(preUni.toString()) // Campo de precio (preUni)
        binding.edtStock.setText(stock.toString()) // Campo de stock
        binding.tvArchivoSeleccionado.text = imgProducto // Campo de imagen (imgProducto)
        binding.etDescripcion.setText(descripcion) // Campo de descripción
        Log.d("DEBUG_CHECKBOX", "Valor de 'estProducto' recibido en fragmento: $estProducto")
        binding.checkBox.isChecked = estProducto
        Log.d("DEBUG_CHECKBOX", "Estado de 'binding.checkBox.isChecked' DESPUÉS de la asignación: ${binding.checkBox.isChecked}")
        Log.d("ActualizarProductoFragment", "Valor de codCategoria recibido: $codCategoria")
        binding.actvCategoria.setText(codCategoria.toString()) // Campo de categoría
        binding.actvMarca.setText(codMarca.toString()) // Campo de marca

        // Cargar imagen existente si hay una URL
        if (imgProducto.isNotEmpty()) {
            val imageUrl = "${com.proyecto.proyectoshopmi.data.client.RetrofitClient.BASE_URL}imagenes/productos/$imgProducto"
            Log.d("ActualizarProductoFragment", "Cargando imagen desde: $imageUrl")
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground) // Un placeholder mientras carga
                .error(R.drawable.ic_launcher_foreground) // Una imagen si hay error de carga
                .into(binding.ivProducto)
            binding.tvArchivoSeleccionado.text = imgProducto
        } else {
            binding.tvArchivoSeleccionado.text = "Ningún archivo seleccionado"
        }


        // Listener para el botón de seleccionar imagen
        binding.btnSeleccionarImagen.setOnClickListener {
            pickImageLauncher.launch("image/*") // Lanza el selector de imágenes
        }

        // Cargar categorías y configurar AutoCompleteTextView
        cargarCategorias()
        // Cargar marcas y configurar AutoCompleteTextView
        cargarMarcas()

        // Listener para el botón de registrar
        binding.btnActualizar.setOnClickListener {
            actualizarProducto()
        }

        // Listener para el botón de cancelar
        binding.btnCancelar.setOnClickListener {
            Toast.makeText(requireContext(), "Operación cancelada", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun cargarCategorias() {
        categoriaService.selectCategorias(
            onSuccess = { data ->
                categorias = data
                Log.d("DEBUG_CATEGORY", "--- INICIO CARGA CATEGORÍAS ---")
                Log.d("DEBUG_CATEGORY", "Categorías cargadas desde API (nombres): ${data.map { it.name }}") // Muestra todos los nombres cargados

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    categorias.map { it.name }
                )
                binding.actvCategoria.setAdapter(adapter)

                // --- PUNTO CRÍTICO DE DEPURACIÓN PARA CATEGORÍA ---
                Log.d("DEBUG_CATEGORY", "Nombre de categoría a buscar (codCategoria): '$codCategoria'")
                val currentCategory = categorias.find { it.name == codCategoria }

                currentCategory?.let {
                    binding.actvCategoria.setText(it.name, false)
                    selectedCategoriaId = it.value
                    Log.d("DEBUG_CATEGORY", "¡ÉXITO! Categoría '${it.name}' (ID: ${it.value}) pre-seleccionada.")
                    Log.d("DEBUG_CATEGORY", "Texto en actvCategoria DESPUÉS de setear: '${binding.actvCategoria.text}'")
                } ?: run {
                    Log.w("DEBUG_CATEGORY", "FALLO: Categoría con nombre '$codCategoria' NO ENCONTRADA en la lista cargada.")
                    binding.actvCategoria.setText("", false) // Limpiar si no se encuentra
                    selectedCategoriaId = null
                }
                Log.d("DEBUG_CATEGORY", "--- FIN CARGA CATEGORÍAS ---")

                binding.actvCategoria.setOnItemClickListener { parent, view, position, id ->
                    selectedCategoriaId = categorias[position].value
                    Log.d("DEBUG_CATEGORY", "Categoría seleccionada por el usuario: ${categorias[position].name} (ID: ${selectedCategoriaId})")
                }
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), "Error al cargar categorías: $errorMessage", Toast.LENGTH_LONG).show()
                Log.e("DEBUG_CATEGORY_ERROR", "Error al cargar categorías: $errorMessage")
            }
        )
    }


    // Función cargarMarcas (asegúrate de que tenga la lógica para pre-seleccionar)
    private fun cargarMarcas() {
        marcaService.selectMarcas(
            onSuccess = { data ->
                marcas = data
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    marcas.map { it.name }
                )
                binding.actvMarca.setAdapter(adapter)

                // ESTO ES CLAVE: Establece el ID si el nombre de marca ya existe
                val currentMarca = marcas.find { it.name == codMarca }
                currentMarca?.let {
                    binding.actvMarca.setText(it.name, false)
                    selectedMarcaId = it.value // Establece el ID aquí
                }

                binding.actvMarca.setOnItemClickListener { parent, view, position, id ->
                    selectedMarcaId = marcas[position].value
                }
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), "Error al cargar marcas: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }


    private fun actualizarProducto() {
        val codProducto = binding.edtCodigo.text.toString().toIntOrNull() ?: 0
        val nombre = binding.edtNombreProducto.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val precio = binding.edtPrecio.text.toString().trim()
        val stock = binding.edtStock.text.toString().trim()
        val imgProducto = binding.tvArchivoSeleccionado.text.toString().trim()
        val codCategoria = selectedCategoriaId ?: categorias.find { it.name == this.codCategoria }?.value
        val codMarca = selectedMarcaId ?: marcas.find { it.name == this.codMarca }?.value
        val estado = binding.checkBox.isChecked

        Log.d("ActualizarProducto", "ID de Categoría a enviar: $codCategoria")
        Log.d("ActualizarProducto", "ID de Marca a enviar: $codMarca")
        Log.d("ActualizarProducto", "Estado del Producto a enviar: $estado")

        if (nombre.isEmpty() || descripcion.isEmpty() || precio.isEmpty() || stock.isEmpty() || codCategoria == null || codMarca == null) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos, incluyendo categoría y marca.", Toast.LENGTH_SHORT).show()
            return
        }

        val preUniValue = precio.toDoubleOrNull()
        val stockValue = stock.toIntOrNull()

        if (preUniValue == null || stockValue == null) {
            Toast.makeText(requireContext(), "Precio y Stock deben ser números válidos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Construir el mapa de datos para el RequestBody
        val precioFormateado = String.format("%.2f", preUniValue).replace(',', '.')
        val productoData = mutableMapOf<String, RequestBody>()
        Log.d("ActualizarProducto", "Datos enviados: \n" +
                "codProducto: $codProducto\n" +
                "imgProducto: $imgProducto\n" +
                "nomProducto: $nombre\n" +
                "descripcion: $descripcion\n" +
                "preUni: $precioFormateado\n" +
                "stock: $stockValue\n" +
                "estProducto: ${estado.toString().lowercase()}\n" +
                "codCategoria: $codCategoria\n" +
                "codMarca: $codMarca"
        )
        productoData["codProducto"] = codProducto.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["imgProducto"] = imgProducto.toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["nomProducto"] = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["descripcion"] = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["preUni"] = precioFormateado.toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["stock"] = stockValue.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["estProducto"] = estado.toString().lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["codCategoria"] = codCategoria.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["codMarca"] = codMarca.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // IMPORTANTE: Si no se seleccionó una nueva imagen, envía la URL de la imagen actual
        if (selectedImageFile == null && imgProducto.isNotEmpty()) {
            productoData["imgActual"] = imgProducto.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        productoService.actualizarProducto(
            imageFile = selectedImageFile,
            productoData = productoData,
            onSuccess = { message ->
                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Éxito!")
                    .setContentText(message)
                    .setConfirmText("OK")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.content_frame, HomeFragment())
                            .commit()
                    }
                    .show()
            },
            onError = { errorMessage ->
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error al actualizar producto.")
                    .setContentText(errorMessage)
                    .setConfirmText("OK")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                    }
                    .show()
            }
        )
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            // Intenta obtener el nombre de columna DISPLAY_NAME del ContentProvider
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = it.getString(columnIndex)
                    }
                }
            }
        }
        // Si no se pudo obtener el nombre del DISPLAY_NAME o no es un Content URI
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "file_unknown" // Devuelve "file_unknown" si no se pudo obtener el nombre
    }

    /**
     * Convierte una URI de contenido en un archivo temporal.
     * Esto es necesario para subir el archivo a un servidor.
     */
    private fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileName = getFileNameFromUri(context, uri)
        val tempFile = File(context.cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al crear archivo temporal: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(
            codProducto: Int,
            nomProducto: String,
            imgProducto: String,
            preUni: Double,
            stock: Int,
            descripcion: String,
            estProducto: Boolean,
            codCategoria: String,
            codMarca: String
        ) = ActualizarProductoFragment().apply {
            arguments = Bundle().apply {
                putInt("codProducto", codProducto)
                putString("nomProducto", nomProducto)
                putString("imgProducto", imgProducto)
                putDouble("preUni", preUni)
                putInt("stock", stock)
                putString("descripcion", descripcion)
                putBoolean("estProducto", estProducto)
                putString("nomCategoria", codCategoria)
                putString("nombreMarca", codMarca)
            }
        }
    }

}

