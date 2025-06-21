package com.proyecto.proyectoshopmi.fragment.producto

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide // Asegúrate de que Glide esté en tus dependencias si lo usas para cargar imágenes
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.service.ProductoService
import com.proyecto.proyectoshopmi.data.model.response.SelectResponse
import com.proyecto.proyectoshopmi.data.service.CategoriaService
import com.proyecto.proyectoshopmi.data.service.MarcaService
import com.proyecto.proyectoshopmi.databinding.FragmentRegistrarProductoBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream // Importar InputStream

class RegistrarProductoFragment : Fragment() {


    private var _binding: FragmentRegistrarProductoBinding? = null
    private val binding get() = _binding!!

    // Instancias de los servicios
    private val productoService = ProductoService()
    private val marcaService = MarcaService()
    private val categoriaService = CategoriaService()

    // Archivo de imagen seleccionado
    private var selectedImageFile: File? = null

    // Launcher para seleccionar imagen de la galería
    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Muestra el nombre del archivo seleccionado
                binding.tvArchivoSeleccionado.text = getFileName(it)
                // Convierte la URI a un archivo temporal para la subida
                selectedImageFile = uriToFile(requireContext(), it)

                // Opcional: Cargar vista previa de la imagen si tienes un ImageView
                // Glide.with(this).load(it).into(binding.imageViewPreview);
            }
        }

    // Listas para almacenar categorías y marcas
    private var categorias: List<SelectResponse> = emptyList()
    private var marcas: List<SelectResponse> = emptyList()

    // IDs de la categoría y marca seleccionadas
    private var selectedCategoriaId: Int? = null
    private var selectedMarcaId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listener para el botón de seleccionar imagen
        binding.btnSeleccionarImagen.setOnClickListener {
            pickImageLauncher.launch("image/*") // Lanza el selector de imágenes
        }

        // Cargar categorías y configurar AutoCompleteTextView
        cargarCategorias()

        // Cargar marcas y configurar AutoCompleteTextView
        cargarMarcas()

        // Listener para el botón de registrar
        binding.btnRegistrar.setOnClickListener {
            guardarCambios()
        }

        // Listener para el botón de cancelar
        binding.btnCancelar.setOnClickListener {
            // Navegar de regreso al Fragment_home
            findNavController().navigate(R.id.action_registrarProductoFragment_to_productoFragment)
            Toast.makeText(requireContext(), "Registro cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Carga las categorías desde el servicio y las configura en el AutoCompleteTextView.
     */
    private fun cargarCategorias() {
        categoriaService.selectCategorias(
            onSuccess = { data ->
                categorias = data
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    categorias.map { it.name } // Mapea los objetos a solo sus nombres para el adaptador
                )
                binding.actvCategoria.setAdapter(adapter)

                binding.actvCategoria.setOnItemClickListener { parent, view, position, id ->
                    // Al seleccionar un ítem, guarda el ID correspondiente
                    selectedCategoriaId = categorias[position].value
                }
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), "Error al cargar categorías: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    /**
     * Carga las marcas desde el servicio y las configura en el AutoCompleteTextView.
     */
    private fun cargarMarcas() {
        marcaService.selectMarcas(
            onSuccess = { data ->
                marcas = data
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    marcas.map { it.name } // Mapea los objetos a solo sus nombres para el adaptador
                )
                binding.actvMarca.setAdapter(adapter)

                binding.actvMarca.setOnItemClickListener { parent, view, position, id ->
                    // Al seleccionar un ítem, guarda el ID correspondiente
                    selectedMarcaId = marcas[position].value
                }
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), "Error al cargar marcas: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    /**
     * Valida los campos del formulario y procede a registrar el producto.
     */
    private fun guardarCambios() {
        // Validaciones de campos obligatorios
        if (binding.editTextText2.text.isNullOrBlank()) {
            Toast.makeText(requireContext(), "El nombre del producto es obligatorio.", Toast.LENGTH_SHORT).show()
            return
        }
        val preUni = binding.editTextText3.text.toString().toDoubleOrNull()
        if (preUni == null) {
            Toast.makeText(requireContext(), "El precio unitario es obligatorio y debe ser un número válido.", Toast.LENGTH_SHORT).show()
            return
        }
        val stock = binding.editTextText4.text.toString().toIntOrNull()
        if (stock == null) {
            Toast.makeText(requireContext(), "El stock es obligatorio y debe ser un número entero válido.", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.etDescripcion.text.isNullOrBlank()) {
            Toast.makeText(requireContext(), "La descripción es obligatoria.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedCategoriaId == null) {
            Toast.makeText(requireContext(), "Debe seleccionar una categoría.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedMarcaId == null) {
            Toast.makeText(requireContext(), "Debe seleccionar una marca.", Toast.LENGTH_SHORT).show()
            return
        }
        // ¡IMPORTANTE! Validación de la imagen
        if (selectedImageFile == null) {
            Toast.makeText(requireContext(), "Debe seleccionar una imagen para el producto.", Toast.LENGTH_SHORT).show()
            return
        }

        // Construcción del mapa de datos para el producto
        val productoData = mutableMapOf<String, RequestBody>()

        // Se convierte el texto del EditText a Int o 0 si está vacío/inválido.
        // Asegúrate que tu backend maneje '0' correctamente si no es un ID autoincremental
        val codProducto = binding.editTextText.text.toString().toIntOrNull() ?: 0
        productoData["codProducto"] = codProducto.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        productoData["nomProducto"] = binding.editTextText2.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["descripcion"] = binding.etDescripcion.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["preUni"] = preUni.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["stock"] = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["estProducto"] = binding.checkBox.isChecked.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // Añade los IDs de categoría y marca si están seleccionados
        selectedCategoriaId?.let {
            productoData["codCategoria"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }
        selectedMarcaId?.let {
            productoData["codMarca"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        // Llamada al servicio para registrar el producto
        productoService.registrarProducto(
            imageFile = selectedImageFile, // Pasa el archivo de imagen
            productoData = productoData,   // Pasa los datos del producto
            onSuccess = { message ->
                // Muestra un SweetAlertDialog de éxito
                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Éxito!")
                    .setContentText(message)
                    .setConfirmText("Aceptar")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        clearForm() // Limpia el formulario después del éxito
                    }
                    .show()
            },
            onError = { errorMessage ->
                // Muestra un SweetAlertDialog de error
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("¡Error!")
                    .setContentText("Error al registrar producto: $errorMessage")
                    .show()
            }
        )
    }

    /**
     * Obtiene el nombre de archivo a partir de una URI.
     * Intenta obtener el DISPLAY_NAME del ContentProvider; si no, usa una parte de la ruta.
     */
    private fun getFileName(uri: Uri): String {
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
        val fileName = getFileName(uri) // Obtén un nombre de archivo
        val tempFile = File(context.cacheDir, fileName) // Crea un archivo temporal en la caché

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream) // Copia el contenido de la URI al archivo temporal
                }
            }
            tempFile // Retorna el archivo temporal
        } catch (e: Exception) {
            e.printStackTrace() // Imprime la traza del error si algo sale mal
            Toast.makeText(context, "Error al crear archivo temporal: ${e.message}", Toast.LENGTH_LONG).show()
            null // Retorna null en caso de error
        }
    }

    /**
     * Limpia todos los campos del formulario y restablece las selecciones.
     */
    private fun clearForm() {
        binding.editTextText.text.clear()
        binding.editTextText2.text.clear()
        binding.editTextText3.text.clear()
        binding.editTextText4.text.clear()
        binding.etDescripcion.text.clear()
        binding.checkBox.isChecked = false
        binding.tvArchivoSeleccionado.text = "Ningún archivo seleccionado"
        selectedImageFile = null
        binding.actvCategoria.text.clear()
        selectedCategoriaId = null
        binding.actvMarca.text.clear()
        selectedMarcaId = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpia el binding para evitar fugas de memoria
    }
}