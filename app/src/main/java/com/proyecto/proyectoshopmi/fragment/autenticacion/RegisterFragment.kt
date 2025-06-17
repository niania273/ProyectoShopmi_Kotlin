package com.proyecto.proyectoshopmi.fragment.autenticacion

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.model.request.UsuarioRequestLogin
import com.proyecto.proyectoshopmi.data.model.request.UsuarioRequestRegistro
import com.proyecto.proyectoshopmi.data.service.UsuarioService
import com.proyecto.proyectoshopmi.databinding.FragmentLoginBinding
import com.proyecto.proyectoshopmi.databinding.FragmentRegisterBinding
import com.proyecto.proyectoshopmi.fragment.HomeFragment
import java.util.*

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var usuarioService: UsuarioService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fechaNacimiento = view.findViewById<EditText>(R.id.edtFecNac)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancel)

        fechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val fecha = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                fechaNacimiento.setText(fecha)
            }, anio, mes, dia)

            datePicker.show()
        }

        btnCancelar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        conectarIniciarSesion()
        registrarUsuario()
    }

    private fun registrarUsuario(){
        usuarioService = UsuarioService(requireContext())

        binding.btnRegister.setOnClickListener {
            val inputFecha = binding.edtFecNac.text.toString()
            val fechaParseada = parsearFecha(inputFecha)
            val sexoSeleccionado = binding.spinnerGenero.selectedItem.toString()
            var sexoParseado: String = ""

            if (fechaParseada == null) {
                Toast.makeText(context, "Formato de fecha inválido. Usa dd/MM/yyyy", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(sexoSeleccionado == "Mujer"){
               sexoParseado = "M"
            } else if (sexoSeleccionado == "Hombre") sexoParseado = "H"

            val usuario = UsuarioRequestRegistro(
                binding.edtDni.text.toString(),
                binding.edtApellidos.text.toString(),
                binding.edtNombres.text.toString(),
                fechaParseada,
                sexoParseado,
                binding.edtTelefono.text.toString(),
                binding.edtCorreo.text.toString(),
                binding.edtContrasenia.text.toString()
            )

            usuarioService.registrarUsuario(
                usuario,
                onSuccess = { registerResponse ->
                    Toast.makeText(context, "¡Listo! $registerResponse", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, LoginFragment())
                        .addToBackStack(null)
                        .commit()
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun conectarIniciarSesion(){
        val fullText = "¿Ya tiene una cuenta? Iniciar sesión"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("Iniciar sesión")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.orange)
                ds.typeface = android.graphics.Typeface.DEFAULT_BOLD

            }
        }

        spannable.setSpan(clickableSpan, start, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLoginPrompt.text = spannable
        binding.tvLoginPrompt.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun parsearFecha(fecha: String): String? {
        return try {
            val sdfEntrada = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val sdfSalida = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            val date = sdfEntrada.parse(fecha)
            sdfSalida.format(date!!)
        } catch (e: Exception) {
            null
        }
    }
}
