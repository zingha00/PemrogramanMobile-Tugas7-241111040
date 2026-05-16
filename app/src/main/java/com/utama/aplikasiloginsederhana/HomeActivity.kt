package com.utama.aplikasiloginsederhana

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val username = intent.getStringExtra("username")
        tvUsername.text = username ?: "Tamu"
    }
}