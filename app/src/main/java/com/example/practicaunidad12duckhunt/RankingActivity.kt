package com.example.practicaunidad12duckhunt

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.practicaunidad12duckhunt.databinding.ActivityRankingBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding
    private var listaUsuarios = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nick: String = intent.getStringExtra("nick") ?: ""
        val cazados: Long = intent.getLongExtra("cazados", 0L)
        listaUsuarios = intent.getSerializableExtra("listaUsuarios") as ArrayList<Usuario>? ?: arrayListOf()

        listaUsuarios.sortByDescending { it.puntos }

        binding.txtPuntuacionJugador.setText("$nick ha cazado $cazados patos")
        binding.txtPrimero.setText("1º - ${listaUsuarios[0].nombre} - ${listaUsuarios[0].puntos} patos")
        binding.txtSegundo.setText("2º - ${listaUsuarios[1].nombre} - ${listaUsuarios[1].puntos} patos")
        binding.txtTercero.setText("3º - ${listaUsuarios[2].nombre} - ${listaUsuarios[2].puntos} patos")
        binding.txtCuarto.setText("4º - ${listaUsuarios[3].nombre} - ${listaUsuarios[3].puntos} patos")
        binding.txtQuinto.setText("5º - ${listaUsuarios[4].nombre} - ${listaUsuarios[4].puntos} patos")

        cambiarTexto()

        binding.btnFinalizar.setOnClickListener {
            val intent = Intent(this@RankingActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnReiniciar.setOnClickListener {
            val intent = Intent(this@RankingActivity, MainActivity::class.java)
            intent.putExtra("accion", "Reiniciar")
            intent.putExtra(Constantes.EXTRA_NICK, nick)
            setResult(RESULT_OK, intent)
            startActivity(intent)
        }
    }

    private fun cambiarTexto() {
        val typeface = Typeface.createFromAsset(assets, "pixel.ttf")
        binding.txtTituloRanking.typeface = typeface
        binding.txtPuntuacionJugador.typeface = typeface
        binding.btnFinalizar.typeface = typeface
        binding.btnReiniciar.typeface = typeface
        binding.txtPrimero.typeface = typeface
        binding.txtSegundo.typeface = typeface
        binding.txtTercero.typeface = typeface
        binding.txtCuarto.typeface = typeface
        binding.txtQuinto.typeface = typeface
    }
}