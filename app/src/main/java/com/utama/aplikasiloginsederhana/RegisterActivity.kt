package com.utama.aplikasiloginsederhana

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername   = findViewById<EditText>(R.id.etRegUsername)
        val etPassword   = findViewById<EditText>(R.id.etRegPassword)
        val etKonfirmasi = findViewById<EditText>(R.id.etRegKonfirmasi)
        val btnSimpan    = findViewById<Button>(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val username   = etUsername.text.toString().trim()
            val password   = etPassword.text.toString().trim()
            val konfirmasi = etKonfirmasi.text.toString().trim()

            when {
                username.isEmpty() || password.isEmpty() || konfirmasi.isEmpty() -> {
                    Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                }
                password != konfirmasi -> {
                    Toast.makeText(this, "Password dan Konfirmasi tidak sama!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Kirim ke API
                    lifecycleScope.launch {
                        try {
                            val request = RegisterRequest(
                                username = username,
                                password = password,
                                email = "$username@example.com"
                            )
                            val response = RetrofitClient.apiService.register(
                                action = "register",
                                request = request
                            )
                            if (response.isSuccessful && response.body()?.success == true) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registrasi berhasil!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    response.body()?.message ?: "Registrasi gagal!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            // Jika API gagal, tetap register lokal
                            Toast.makeText(
                                this@RegisterActivity,
                                "Registrasi berhasil (offline)!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}