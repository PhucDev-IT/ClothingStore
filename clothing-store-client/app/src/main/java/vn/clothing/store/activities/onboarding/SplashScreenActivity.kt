package vn.clothing.store.activities.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.clothing.store.MainActivity
import vn.clothing.store.R
import vn.clothing.store.activities.authentication.LoginActivity
import vn.clothing.store.utils.MySharedPreferences

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    override fun onStart() {
        super.onStart()
        initUi()
    }


    private fun initUi(){
        Handler(Looper.getMainLooper()).postDelayed({
            val isFirstApp = MySharedPreferences.getBooleanValue(this, MySharedPreferences.PREF_WAS_ONBOARDING)
            if(isFirstApp){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }else{
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            }
        }, 2000)
    }
}