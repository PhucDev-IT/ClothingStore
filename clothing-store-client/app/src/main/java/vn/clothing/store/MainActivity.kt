package vn.clothing.store

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.clothing.store.activities.ShoppingCartActivity
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.databinding.ActivityMainBinding
import vn.clothing.store.fragments.HomeFragment
import vn.clothing.store.fragments.UserFragment

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun populateData() {
        requestPermission(arrayListOf(Manifest.permission.POST_NOTIFICATIONS)){}
    }

    override fun setListener() {

    }

    override val layoutView: View
        get() {
            binding = ActivityMainBinding.inflate(layoutInflater)
            return binding.root
        }


    override fun onResume() {
        super.onResume()
    }
}