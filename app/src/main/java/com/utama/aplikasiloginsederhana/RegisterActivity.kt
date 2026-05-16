package com.utama.aplikasiloginsederhana

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername   = findViewById<EditText>(R.id.etRegUsername)
        val etPassword   = findViewById<EditText>(R.id.etRegPassword)
        val etKonfirmasi = findViewById<EditText>(R.id.etRegKonfirmasi)
        val btnSimpan    = findViewById<Button>(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val username   = etUsername.text.toString()
            val password   = etPassword.text.toString()
            val konfirmasi = etKonfirmasi.text.toString()

            when {
                username.isEmpty() || password.isEmpty() || konfirmasi.isEmpty() -> {
                    Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                }
                password != konfirmasi -> {
                    Toast.makeText(this, "Password dan Konfirmasi tidak sama!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
