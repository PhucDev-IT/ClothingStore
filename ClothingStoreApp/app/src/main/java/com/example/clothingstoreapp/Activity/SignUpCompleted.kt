package com.example.clothingstoreapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ActivitySignUpCompletedBinding

class SignUpCompleted : AppCompatActivity() {
    private lateinit var binding:ActivitySignUpCompletedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this,LoginScreen::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun initView(){
        val email = intent.getStringExtra("email")
        binding.tvEmail.text = email
    }


}