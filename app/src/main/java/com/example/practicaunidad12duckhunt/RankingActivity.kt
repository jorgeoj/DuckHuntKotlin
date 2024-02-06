package com.example.practicaunidad12duckhunt

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practicaunidad12duckhunt.databinding.ActivityRankingBinding

//TODO Queda hacer el ranking (xml y el imprimirlo)
class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nick = intent.getStringExtra("nick")
        val cazados = intent.getLongExtra("cazados", 0L)
        binding.txtPuntuacionJugador.setText("$nick ha cazado $cazados patos")

        cambiarTexto()

        binding.btnFinalizar.setOnClickListener {
            val intent = Intent(this@RankingActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        // TODO Cambiar esto porque explota
        binding.btnReiniciar.setOnClickListener {
            val intent = Intent(this@RankingActivity, MainActivity::class.java)
            intent.putExtra("nick", nick)
            startActivity(intent)
        }
    }

    private fun cambiarTexto() {
        val typeface = Typeface.createFromAsset(assets, "pixel.ttf")
        binding.txtTituloRanking.typeface = typeface
        binding.txtPuntuacionJugador.typeface = typeface
        binding.btnFinalizar.typeface = typeface
        binding.btnReiniciar.typeface = typeface
    }
}