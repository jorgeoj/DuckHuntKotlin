package com.example.practicaunidad12duckhunt

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practicaunidad12duckhunt.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var nick:String
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Para la fuente
        val typeface = Typeface.createFromAsset(assets, "pixel.ttf")
        binding.etNick.typeface=typeface
        binding.btnStart.typeface=typeface

        // Accion al darle al boton de empezar
        binding.btnStart.setOnClickListener {
            nick = binding.etNick.text.toString()
            // Controlar que se pone un nick o que tiene mas de 3 caracteres
            when {
                nick.isEmpty() -> {
                    binding.etNick.error = "El nick es obligatorio"
                }
                nick.length < 3 -> {
                    binding.etNick.error = "Debe tener al menos 3 caracteres"
                }
                else -> {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra(Constantes.EXTRA_NICK, nick)
                    startActivity(intent)
                }
            }
        }
    }
}