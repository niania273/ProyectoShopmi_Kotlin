package com.proyecto.proyectoshopmi

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.proyectoshopmi.fragment.HomeFragment
import com.proyecto.proyectoshopmi.fragment.autenticacion.LoginFragment
import com.proyecto.proyectoshopmi.fragment.producto.ListarProductosFragment

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mostrarFragment(HomeFragment())
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val statusBarHeight = resources.getDimensionPixelSize(
            resources.getIdentifier("status_bar_height", "dimen", "android")
        )
        toolbar.setPadding(0, statusBarHeight, 0, 0)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)

        findViewById<ImageButton>(R.id.btnUser).setOnClickListener {
            mostrarDialogoUsuario()
        }

        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener {
            toggleMenuDerecho()
        }

        findViewById<ImageView>(R.id.logoImage).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
        setupMenuButtons()
    }

    fun mostrarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .addToBackStack(null) // para poder volver con el botón atrás
            .commit()
    }

    private fun mostrarDialogoUsuario() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Iniciar sesión")
            .setMessage("Debes iniciar sesión para continuar.")
            .setPositiveButton("Ir a Iniciar Sesión") { _, _ ->
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cancelar", null)
            .show()

    }

    private fun toggleMenuDerecho() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setupMenuButtons() {
        val btnSmartphones = findViewById<Button>(R.id.btnSmartphones)
        val btnComputo = findViewById<Button>(R.id.btnComputo)
        val btnAudio = findViewById<Button>(R.id.btnAudio)
        val btnGaming = findViewById<Button>(R.id.btnGaming)
        val btnHogarInteligente = findViewById<Button>(R.id.btnHogarInteligente)
        val btnFotografia = findViewById<Button>(R.id.btnFotografia)

        btnSmartphones.setOnClickListener { abrirFragmentPorCategoria(1) }
        btnComputo.setOnClickListener { abrirFragmentPorCategoria(2) }
        btnAudio.setOnClickListener { abrirFragmentPorCategoria(3) }
        btnGaming.setOnClickListener { abrirFragmentPorCategoria(4) }
        btnHogarInteligente.setOnClickListener { abrirFragmentPorCategoria(5) }
        btnFotografia.setOnClickListener { abrirFragmentPorCategoria(6) }
    }

    private fun abrirFragmentPorCategoria(idCategoria: Int) {
        val fragment = ListarProductosFragment().apply {
            arguments = Bundle().apply {
                putInt("categoria_id", idCategoria)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .addToBackStack(null)
            .commit()
    }
}