package com.example.clothingstoreapp


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.clothingstoreapp.Activity.Checkout_Screen
import com.example.clothingstoreapp.Activity.MyCartScreen
import com.example.clothingstoreapp.Activity.ProductsIsLikedScreen
import com.example.clothingstoreapp.FragmentLayout.Add_AddressFragment
import com.example.clothingstoreapp.FragmentLayout.HomeFragment
import com.example.clothingstoreapp.FragmentLayout.NotificationFragment
import com.example.clothingstoreapp.FragmentLayout.UserFragment
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Service.CustomerService
import com.example.clothingstoreapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import android.Manifest
import android.content.ContentValues.TAG
import android.util.Log
import com.example.clothingstoreapp.Service.VoucherService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val RC_NOTIFICATIONS = 99
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserCurrent()
        registerFCM()
        askNotificationPermission()



        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.body_container, HomeFragment())
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.shopping_cart -> {

                    val intent = Intent(this, MyCartScreen::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.notification -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.body_container, NotificationFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
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
        // Hiển thị Fragment mặc định khi chạy app
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.body_container, HomeFragment())
                .commit()
        }


    }

    //Lấy thông tin người dùng hện tại
    private fun getUserCurrent(){
        val uid = com.google.firebase.ktx.Firebase.auth.currentUser?.uid
        if (uid != null) {
            val db = Firebase.firestore
            val customerService = CustomerService(db)
            customerService.getInformationUser(uid){user->
                if (user != null) {
                    UserManager.getInstance().setUser(user)
                }
            }
        }
    }


    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),RC_NOTIFICATIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == RC_NOTIFICATIONS){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Allow",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"NO",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun registerFCM(){
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/allDevices")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Đăng ký thành công")
                } else {
                    Log.e(TAG, "Đăng ký thất bại")
                }
            }

    }

    //Lấy vị trí hiện tại
    private fun getLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }
}