package com.proyecto.proyectoshopmi

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setActivityContent(R.layout.activity_register)

        val fechaNacimiento = findViewById<EditText>(R.id.editTextFechaNacimiento)
        val btnCancelar = findViewById<Button>(R.id.btnCancel)

        fechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val fecha = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                fechaNacimiento.setText(fecha)
            }, anio, mes, dia)

            datePicker.show()
        }

        btnCancelar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}