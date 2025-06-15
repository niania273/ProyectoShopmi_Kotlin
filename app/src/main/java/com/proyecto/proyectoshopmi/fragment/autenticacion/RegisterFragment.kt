package com.proyecto.proyectoshopmi.fragment.autenticacion

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.proyecto.proyectoshopmi.R
import java.util.*

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
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
            parentFragmentManager.popBackStack() // Vuelve al fragment anterior (HomeFragment)
        }
    }
}
