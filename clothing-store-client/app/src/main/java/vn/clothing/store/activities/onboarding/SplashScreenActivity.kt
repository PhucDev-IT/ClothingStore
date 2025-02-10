package vn.clothing.store.activities.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.clothing.store.MainActivity
import vn.clothing.store.R
import vn.clothing.store.activities.authentication.LoginActivity
import vn.clothing.store.common.AppManager
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.utils.MySharedPreferences

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setModeColor()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUi()
    }



    private fun initUi(){
        Handler(Looper.getMainLooper()).postDelayed({
            val isFirstApp = MySharedPreferences.getBooleanValue(this, MySharedPreferences.PREF_WAS_ONBOARDING)
            if(isFirstApp){
                checkUser()
            }else{
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            }
        }, 2000)
    }

    private fun setModeColor(){
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val savedMode = MySharedPreferences.getIntValues(this, MySharedPreferences.PREF_KEY_THEME_MODE)

        if (currentMode != savedMode) {
            AppCompatDelegate.setDefaultNightMode(savedMode)
        }
    }


    private fun checkUser(){
        CoroutineScope(Dispatchers.IO).launch {
            val listDeferred = async {
                APPDATABASE.userDao().getAll()
            }
            val tokenDeferred = async {
                MySharedPreferences.getStringValues(this@SplashScreenActivity, MySharedPreferences.PREF_TOKEN)
            }
            val list = listDeferred.await()
            val token = tokenDeferred.await()

            withContext(Dispatchers.Main) {
                if(list.isEmpty() || token == null){
                    val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }else{
                    AppManager.token = token
                    AppManager.user = list[0]
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }
    }
}