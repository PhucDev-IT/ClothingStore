package com.example.clothingstoreadmin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.clothingstoreadmin.activity.ChatScreen
import com.example.clothingstoreadmin.FragmentLayout.CouponFragment
import com.example.clothingstoreadmin.FragmentLayout.HomeFragment
import com.example.clothingstoreadmin.FragmentLayout.ManagerProductFragment
import com.example.clothingstoreadmin.FragmentLayout.OrderFragment
import com.example.clothingstoreadmin.databinding.ActivityMainBinding
import com.example.clothingstoreadmin.model.UserManager
import com.example.clothingstoreadmin.service.CustomerService
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth

class MainActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding:ActivityMainBinding
    private var currentFragment:Fragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle:ActionBarDrawerToggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,
            R.string.navigation_draw_open,R.string.navigation_draw_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        replaceFragment(HomeFragment())

        binding.navigationView.menu.findItem(R.id.nav_dashboard).isChecked = true


        getUserCurrent()
        handleClick()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_dashboard -> {
                replaceFragment(HomeFragment())
                binding.toolbar.title = "Trang chủ"
            }

            R.id.nav_product -> {
                replaceFragment(ManagerProductFragment())
                binding.toolbar.title = "Sản phẩm"
            }

            R.id.nav_coupon -> replaceFragment(CouponFragment())

            R.id.nav_order -> replaceFragment(OrderFragment())

        }


        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    @SuppressLint("CommitTransaction")
    private fun replaceFragment(fragment:Fragment){

        if(fragment != currentFragment){

            currentFragment = fragment

            val transaction:FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout,fragment)
            transaction.commit()

        }
    }


    private fun handleClick(){
        binding.btnChat.setOnClickListener {
            val intent = Intent(this,ChatScreen::class.java)
            startActivity(intent)
        }
    }

    //Lấy thông tin người dùng hện tại
    private fun getUserCurrent(){
        val uid = com.google.firebase.ktx.Firebase.auth.currentUser?.uid
        if (uid != null) {
            val customerService = CustomerService()
            customerService.getInformationUser(uid){user->
                if (user != null) {
                    UserManager.getInstance().setUser(user)
                }
            }
        }
    }

}