package com.example.appmenumakanan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appmenumakanan.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var preferences: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = PreferencesHelper(this)

        val email = preferences.getString("email", "Tidak ditemukan")
        val username = preferences.getString("username", "Tidak ditemukan")
        binding.tvEmail.text = "Email: $email"
        binding.tvUsername.text = "Username: $username"

        binding.btnLogout.setOnClickListener {
            preferences.save("isLoggedIn", false)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
