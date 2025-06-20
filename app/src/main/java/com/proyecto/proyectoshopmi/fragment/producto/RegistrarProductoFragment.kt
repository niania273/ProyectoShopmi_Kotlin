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
import com.bumptech.glide.Glide
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.service.ProductoService
import com.proyecto.proyectoshopmi.data.model.response.SelectResponse
import com.proyecto.proyectoshopmi.databinding.FragmentRegistrarProductoBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class RegistrarProductoFragment : Fragment() {

    private var _binding: FragmentRegistrarProductoBinding? = null
    private val binding get() = _binding!!

    private val productoService = ProductoService()
    private var selectedImageFile: File? = null

    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.tvArchivoSeleccionado.text = getFileName(it)
                selectedImageFile = uriToFile(requireContext(), it)
            }
        }

    private var categorias: List<SelectResponse> = emptyList()
    private var marcas: List<SelectResponse> = emptyList()

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

        binding.btnSeleccionarImagen.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Cargar categorías y configurar AutoCompleteTextView
        productoService.selectCategorias(
            onSuccess = { data ->
                categorias = data
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    // *** CORREGIDO AQUÍ: Usar 'it.text' ***
                    categorias.map { it.text }
                )
                binding.actvCategoria.setAdapter(adapter)

                binding.actvCategoria.setOnItemClickListener { parent, view, position, id ->
                    selectedCategoriaId = categorias[position].value
                }
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), "Error al cargar categorías: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )

        // Cargar marcas y configurar AutoCompleteTextView
        productoService.selectMarcas(
            onSuccess = { data ->
                marcas = data
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    // *** CORREGIDO AQUÍ: Usar 'it.text' ***
                    marcas.map { it.text }
                )
                binding.actvMarca.setAdapter(adapter)

                binding.actvMarca.setOnItemClickListener { parent, view, position, id ->
                    selectedMarcaId = marcas[position].value
                }
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), "Error al cargar marcas: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )

        binding.btnRegistrar.setOnClickListener {
            guardarCambios()
        }

        binding.btnCancelar.setOnClickListener {
            Toast.makeText(requireContext(), "Operación cancelada", Toast.LENGTH_SHORT).show()
            clearForm()
        }
    }

    private fun guardarCambios() {
        if (binding.editTextText2.text.isNullOrBlank()) {
            Toast.makeText(requireContext(), "El nombre del producto es obligatorio.", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.editTextText3.text.isNullOrBlank() || binding.editTextText3.text.toString().toDoubleOrNull() == null) {
            Toast.makeText(requireContext(), "El precio unitario es obligatorio y debe ser un número.", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.editTextText4.text.isNullOrBlank() || binding.editTextText4.text.toString().toIntOrNull() == null) {
            Toast.makeText(requireContext(), "El stock es obligatorio y debe ser un número entero.", Toast.LENGTH_SHORT).show()
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

        val productoData = mutableMapOf<String, RequestBody>()

        val codProducto = binding.editTextText.text.toString().toIntOrNull() ?: 0
        productoData["codProducto"] = codProducto.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["nomProducto"] = binding.editTextText2.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["descripcion"] = binding.etDescripcion.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["preUni"] = binding.editTextText3.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["stock"] = binding.editTextText4.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        productoData["estProducto"] = binding.checkBox.isChecked.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        selectedCategoriaId?.let {
            productoData["codCategoria"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }
        selectedMarcaId?.let {
            productoData["codMarca"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        productoService.registrarProducto(
            imageFile = selectedImageFile,
            productoData = productoData,
            onSuccess = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                clearForm()
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), "Error al registrar producto: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
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
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "file_unknown"
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileName = getFileName(uri)
        val tempFile = File(context.cacheDir, fileName)
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

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
        _binding = null
    }
}
