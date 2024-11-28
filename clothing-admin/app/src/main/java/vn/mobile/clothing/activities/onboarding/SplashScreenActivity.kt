package vn.mobile.clothing.activities.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.mobile.clothing.MainActivity
import vn.mobile.clothing.R
import vn.mobile.clothing.activities.authentication.LoginActivity
import vn.mobile.clothing.common.AppManager
import vn.mobile.clothing.database.AppDatabase.Companion.APPDATABASE
import vn.mobile.clothing.utils.MySharedPreferences

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
                checkUser()
            }else{
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            }
        }, 2000)
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