package com.example.clothingstoreapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.clothingstoreapp.Data_Local.DataLocalManager
import com.example.clothingstoreapp.FragmentLayout.HomeFragment
import com.example.clothingstoreapp.MainActivity
import com.example.clothingstoreapp.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = Handler()
        handler.postDelayed({ nextActivity() }, 2000) // đợi 1 giây trước khi chạy Runnable
    }

    private fun nextActivity(){

        //Kiểm tra có phải lần đầu sử dụng ứng dụng ?
        if(DataLocalManager.checkIsFirstUseApp()){

            val intent = Intent(this,OnBoardingActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }else{
            val user = Firebase.auth.currentUser
            var intent = Intent(this, MainActivity::class.java)
            if (user == null) {
                intent = Intent(this,LoginScreen::class.java)
            }
            startActivity(intent)
            finishAffinity()
        }


    }

}