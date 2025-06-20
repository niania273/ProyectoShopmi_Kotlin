package com.proyecto.proyectoshopmi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.proyectoshopmi.fragment.HomeFragment
import com.proyecto.proyectoshopmi.fragment.autenticacion.LoginFragment
import com.proyecto.proyectoshopmi.fragment.producto.ListarProductosFragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.proyecto.proyectoshopmi.fragment.pedido.CarritoComprasFragment
import com.proyecto.proyectoshopmi.fragment.pedido.ListarPedidosFragment
import com.proyecto.proyectoshopmi.helper.LoginListener
import com.proyecto.proyectoshopmi.helper.SessionManager

class MainActivity : AppCompatActivity(), LoginListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        if (savedInstanceState == null && intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == 0) {
            sessionManager.cerrarSesion()
        }

        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupMenuButtons()

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        mostrarFragment(HomeFragment())

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
    }

    override fun onResume() {
        super.onResume()
        setupMenuButtons()
    }

    fun mostrarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .addToBackStack(null)
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
        val btnTienda = findViewById<Button>(R.id.btnTienda)
        val btnSmartphones = findViewById<Button>(R.id.btnSmartphones)
        val btnComputo = findViewById<Button>(R.id.btnComputo)
        val btnAudio = findViewById<Button>(R.id.btnAudio)
        val btnGaming = findViewById<Button>(R.id.btnGaming)
        val btnHogarInteligente = findViewById<Button>(R.id.btnHogarInteligente)
        val btnFotografia = findViewById<Button>(R.id.btnFotografia)

        var llTienda = findViewById<LinearLayout>(R.id.llTienda)
        var llSmartphones = findViewById<LinearLayout>(R.id.llSmartphones)
        var llAudio = findViewById<LinearLayout>(R.id.llAudio)
        var llVisitanos = findViewById<LinearLayout>(R.id.llVisitanos)

        btnTienda.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, HomeFragment())
                .addToBackStack(null)
                .commit()
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        btnSmartphones.setOnClickListener {
            abrirFragmentPorCategoria(1)
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        btnComputo.setOnClickListener {
            abrirFragmentPorCategoria(2)
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        btnAudio.setOnClickListener {
            abrirFragmentPorCategoria(3)
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        btnGaming.setOnClickListener {
            abrirFragmentPorCategoria(4)
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        btnHogarInteligente.setOnClickListener {
            abrirFragmentPorCategoria(5)
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        btnFotografia.setOnClickListener {
            abrirFragmentPorCategoria(6)
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        llTienda.setOnClickListener {
            val fragment = HomeFragment()
            val bundle = Bundle().apply {
                putInt("categoria_id", -1)
            }
            fragment.arguments = bundle
            mostrarFragment(fragment)
        }

        llSmartphones.setOnClickListener {
            val fragment = ListarProductosFragment()
            val bundle = Bundle().apply {
                putInt("categoria_id", 1)
            }
            fragment.arguments = bundle
            mostrarFragment(fragment)
        }

        llAudio.setOnClickListener {
            val fragment = ListarProductosFragment()
            val bundle = Bundle().apply {
                putInt("categoria_id", 3)
            }
            fragment.arguments = bundle
            mostrarFragment(fragment)
        }

        llVisitanos.setOnClickListener {
            val url = "https://maps.app.goo.gl/VxynBgsrK1aCcwNB9"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.setPackage("com.android.chrome")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se encontró un navegador para abrir la ubicación.", Toast.LENGTH_SHORT).show()
                intent.setPackage(null)
                startActivity(intent)
            }
        }
        mostrarBotonPorRol()
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

    override fun onLoginSuccess() {
        setupMenuButtons()
    }

    override fun onStop() {
        super.onStop()

        if (!isChangingConfigurations) {
            sessionManager.cerrarSesion()
            Log.d("MainActivity", "Sesión cerrada porque se minimizó la app")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.cerrarSesion()
        Log.d("MainActivity", "Sesión cerrada automáticamente al destruir el Activity")
        Toast.makeText(this, "onDestroy ejecutado", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarBotonPorRol() {
        val btnShop = findViewById<ImageButton>(R.id.btnShop)
        val btnPedidos = findViewById<ImageButton>(R.id.btnPedidos)
        val usuario = sessionManager.obtenerUsuario()

        btnShop.visibility = View.GONE
        btnPedidos.visibility = View.GONE

        if (usuario != null) {
            if (usuario.rolId == 1){
                btnShop.visibility = View.VISIBLE
                btnShop.setOnClickListener {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, CarritoComprasFragment())
                        .addToBackStack(null)
                        .commit()
                }
            } else if(usuario.rolId == 2 || usuario.rolId == 3){
                btnPedidos.visibility = View.VISIBLE
                btnPedidos.setOnClickListener {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, ListarPedidosFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

}