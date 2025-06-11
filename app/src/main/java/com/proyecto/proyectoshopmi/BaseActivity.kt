package com.proyecto.proyectoshopmi

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

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
    }

    private fun mostrarDialogoUsuario() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Iniciar sesión")
            .setMessage("Debes iniciar sesión para continuar.")
            .setPositiveButton("Ir a Iniciar Sesión") { _, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
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

    /** Inyecta el layout de la actividad hija en el content_frame */
    protected fun setActivityContent(layoutResId: Int) {
        val frame = findViewById<FrameLayout>(R.id.content_frame)
        layoutInflater.inflate(layoutResId, frame, true)
    }
}