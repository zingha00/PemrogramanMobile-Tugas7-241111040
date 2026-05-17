package com.utama.aplikasiloginsederhana

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Username dan Password tidak boleh kosong!",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.login(
                        action = "login",
                        request = LoginRequest(username, password)
                    )
                    if (response.isSuccessful && response.body()?.success == true) {
                        val user = response.body()?.data
                        sessionManager.saveLoginSession(username, user?.id ?: 0)
                        Toast.makeText(this@MainActivity,
                            "Login berhasil!",
                            Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity,
                            response.body()?.message ?: "Username atau password salah!",
                            Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Offline fallback
                    sessionManager.saveLoginSession(username, 0)
                    Toast.makeText(this@MainActivity,
                        "Login berhasil (offline)!",
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}