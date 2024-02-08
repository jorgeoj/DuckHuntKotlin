package com.example.practicaunidad12duckhunt

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.practicaunidad12duckhunt.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var nick: String
    private var cazados:Long = 0
    private var anchoPantalla = 0
    private var altoPantalla = 0
    private var gameOver = false
    private var aleatorio: Random = Random()
    private var listaUsuarios = mutableListOf<Usuario>()
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentLaunch=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
            Log.d("iLaunch", "he entrado en el intentLaunch")
            if (result.resultCode == RESULT_OK){
                val accion = result.data?.extras?.getString("accion")
                when (accion) {
                    "Salir" -> finish()
                    "Reiniciar" -> {
                        nick = result.data?.extras?.getString("nick")!!
                        cazados = 0
                        binding.tvCounter.text = "0"
                        gameOver = false
                        initCuentaAtras()
                        moverPato()
                    }
                }
            }
        }

        leerDatos()
        initPantalla()
        inicializarComponentesVisuales()
        eventos()
        moverPato()
        initCuentaAtras()
    }

    private fun inicializarComponentesVisuales() {
        val typeface = Typeface.createFromAsset(assets, "pixel.ttf")
        binding.tvCounter.typeface = typeface
        binding.tvTimer.typeface = typeface
        binding.tvNick.typeface = typeface
        val extras = intent.extras
        nick = extras!!.getString(Constantes.EXTRA_NICK)!!
        binding.tvNick.text = nick
    }

    private fun eventos() {
        binding.ivPato.setOnClickListener {
            if (!gameOver) {
                cazados++
                binding.tvCounter.text=cazados.toString()
                binding.ivPato.setImageResource(R.drawable.duck_clicked)
                Handler().postDelayed({
                    binding.ivPato.setImageResource(R.drawable.duck)
                    moverPato()
                }, 500)
            }
        }
    }

    private fun initPantalla() {
        val dm1 = resources.displayMetrics
        anchoPantalla=dm1.widthPixels
        altoPantalla=dm1.heightPixels
    }

    private fun moverPato(){
        val maximoX = anchoPantalla - binding.ivPato.width*2
        val maximoY = altoPantalla - binding.ivPato.height*2
        //Generamos un número aleatorio para la coordenada X y otro para la Y
        val randomX = aleatorio.nextInt(maximoX+1 )
        val randomY = aleatorio.nextInt(maximoY+1)
        //Utilizamos los números aleatorios para mover el pato
        binding.ivPato.x = randomX.toFloat()
        binding.ivPato.y = randomY.toFloat()
    }

    private fun initCuentaAtras(){
        object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundosRestantes = millisUntilFinished / 1000
                val texto = "${segundosRestantes}s"
                binding.tvTimer.text = texto
            }
            override fun onFinish() {
                binding.tvTimer.text = getString(R.string.ceroseg)
                gameOver = true
                mostrarDialogoGameOver()
            }
        }.start()
    }

    private fun mostrarDialogoGameOver() {
        /*
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Has cazado $cazados patos").setTitle("Fin del juego")
        builder.setCancelable(false)
        builder.setPositiveButton(
            R.string.reiniciar
        ) { dialog, which ->
            cazados = 0
            binding.tvCounter.text = "0"
            gameOver = false
            initCuentaAtras()
            moverPato()
        }
        builder.setNegativeButton(
            R.string.salir
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        val dialogo = builder.create()
        dialogo.show()
        */

        val intent = Intent(this@MainActivity, RankingActivity::class.java)
        intent.putExtra("nick", nick)
        intent.putExtra("cazados", cazados)
        transferirDatos()
        val usuariosSerializable = ArrayList(listaUsuarios)
        intent.putExtra("listaUsuarios", usuariosSerializable)

        startActivity(intent)
    }

    // Metodos para fireBase

    private fun leerDatos() {
        var nombre: String
        var puntos: Long
        var usuario: Usuario
        val db = Firebase.firestore
        db.collection("puntuaciones").get().addOnSuccessListener { result ->
            for (document in result) {
                nombre = document.id
                puntos = document.data.get("puntos") as Long
                usuario = Usuario(nombre, puntos)
                listaUsuarios.add(usuario)
            }
        }.addOnFailureListener { error ->
            Log.e("FirebaseError", error.message.toString())
        }
    }

    private fun actualizarDatos(usuario: Usuario) {
        val db = Firebase.firestore
        db.collection("puntuaciones").document(usuario.nombre).update("puntos", usuario.puntos).addOnSuccessListener {
            Log.i("Firebase", "Datos actualizados correctamente")
        }.addOnFailureListener { error ->
            Log.e("FirebaseError", error.message.toString())
        }
    }

    private fun insertarDatos(usuario: Usuario) {
        val db = Firebase.firestore
        val datos = hashMapOf("puntos" to usuario.puntos)
        db.collection("puntuaciones").document(usuario.nombre).set(datos).addOnSuccessListener {
            Log.i("Firebase", "Datos actualizados correctamente")
        }.addOnFailureListener { error ->
            Log.e("FirebaseError", error.message.toString())
        }
    }

    private fun transferirDatos(): Boolean {
        val usuarioExistente = listaUsuarios.find { it.nombre == nick }

        if (usuarioExistente != null) {
            // El usuario ya existe en la lista
            if (usuarioExistente.puntos < cazados) {
                // Actualizar los puntos si la nueva puntuación es mayor
                usuarioExistente.puntos = cazados
                actualizarDatos(usuarioExistente)
                return true
            }
            // No es necesario actualizar los puntos si la nueva puntuación es menor o igual
            return false
        } else {
            // El usuario no existe en la lista, así que lo añadimos
            val nuevoUsuario = Usuario(nick, cazados)
            listaUsuarios.add(nuevoUsuario)
            insertarDatos(nuevoUsuario)
            return false
        }
    }


}