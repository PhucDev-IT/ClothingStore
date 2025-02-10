package vn.clothing.store

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import vn.clothing.store.activities.NotificationActivity
import vn.clothing.store.activities.ScanQrCodeActivity
import vn.clothing.store.activities.ShoppingCartActivity
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.databinding.ActivityMainBinding
import vn.clothing.store.databinding.PopupDebugBinding
import vn.clothing.store.fragments.HomeFragment
import vn.clothing.store.fragments.UserFragment
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.utils.MySharedPreferences
import vn.clothing.store.utils.Utils

class MainActivity : BaseActivity() {

    private lateinit var binding:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.body_container, HomeFragment())
                .commit()
        }

    }

    override fun initView() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        if(BuildConfig.DEBUG){
           //checkData()
        }

        Glide.with(this).load(R.drawable.scan_qr).into(binding.btnScanQr)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.body_container, HomeFragment())
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.shopping_cart -> {
                    val intent = Intent(this@MainActivity, ShoppingCartActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener false
                }

                R.id.notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                    return@setOnNavigationItemSelectedListener false
                }

                R.id.user -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.body_container, UserFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                else -> { return@setOnNavigationItemSelectedListener false}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun populateData() {
        requestPermission(arrayListOf(Manifest.permission.POST_NOTIFICATIONS)){}
    }

    override fun setListener() {
        binding.btnScanQr.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanQrCodeActivity::class.java)
            startActivity(intent)
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityMainBinding.inflate(layoutInflater)
            return binding.root
        }


    override fun onResume() {
        super.onResume()
    }

    private fun checkData(){
        val url = MySharedPreferences.getStringValues(this, MySharedPreferences.PREF_KEY_URL)
        if(url==null){
            showDialogDebug()
        }
        APISERVICE.setBaseUrl(url!!)
    }

    private fun showDialogDebug() {
        val url = MySharedPreferences.getStringValues(this, MySharedPreferences.PREF_KEY_URL)
        if(url!=null) return
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        val bindingDialog = PopupDebugBinding.inflate(LayoutInflater.from(this))
        dialog!!.setContentView(bindingDialog.root)

        bindingDialog.btnConfirm.setOnClickListener {
            if(bindingDialog.edtUrl.text.toString().isEmpty()){
                bindingDialog.edtUrl.error = "Vui lòng nhập URL"
                return@setOnClickListener
            }
            APISERVICE.setBaseUrl(bindingDialog.edtUrl.text.toString())
            MySharedPreferences.setStringValue(this, MySharedPreferences.PREF_KEY_URL,bindingDialog.edtUrl.text.toString())
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

}