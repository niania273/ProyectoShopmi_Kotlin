package com.proyecto.proyectoshopmi.fragment.autenticacion

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.proyectoshopmi.R
import com.proyecto.proyectoshopmi.data.model.request.UsuarioRequestLogin
import com.proyecto.proyectoshopmi.data.service.UsuarioService
import com.proyecto.proyectoshopmi.databinding.FragmentLoginBinding
import com.proyecto.proyectoshopmi.fragment.HomeFragment
import com.proyecto.proyectoshopmi.helper.LoginListener


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var usuarioService: UsuarioService
    private var loginListener: LoginListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuarioService = UsuarioService(requireContext())

        requireActivity().title = "Iniciar Sesión"

        binding.btnIniciarSesion.setOnClickListener {
            val usuario = UsuarioRequestLogin(
                binding.edtCorreo.text.toString(),
                binding.edtContrasenia.text.toString()
            )
            usuarioService.iniciarSesionUsuario(
                usuario,
                onSuccess = { loginResponse ->
                    Toast.makeText(context, "Bienvenido/a ${loginResponse.usuario.nombre}", Toast.LENGTH_SHORT).show()
                    loginListener?.onLoginSuccess()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, HomeFragment())
                        .addToBackStack(null)
                        .commit()
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }

        val fullText = "¿No tiene una cuenta? Registrarse"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("Registrarse")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, RegisterFragment())
                    .addToBackStack(null)
                    .commit()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.orange)
                ds.typeface = android.graphics.Typeface.DEFAULT_BOLD
                ds.typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
        }

        spannable.setSpan(clickableSpan, start, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvRegisterPrompt.text = spannable
        binding.tvRegisterPrompt.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginListener) {
            loginListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        loginListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
