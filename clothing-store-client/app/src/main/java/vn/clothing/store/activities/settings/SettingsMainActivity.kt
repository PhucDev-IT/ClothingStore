package vn.clothing.store.activities.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.clothing.store.R
import vn.clothing.store.activities.authentication.LoginActivity
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.databinding.ActivitySettingsMainBinding

class SettingsMainActivity : BaseActivity() {
    private lateinit var binding:ActivitySettingsMainBinding

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun populateData() {
      binding.header.tvName.text = getString(R.string.title_header_settings)

    }

    override fun setListener() {
       binding.tvShippingAddress.setOnClickListener {
           startActivity(Intent(this,AddressActivity::class.java))
       }

        binding.header.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.btnLogout.setOnClickListener {
         PopupDialog.showDialog(this@SettingsMainActivity,PopupDialog.PopupType.CONFIRM,getString(R.string.app_name),getString(R.string.content_logout)){
             APPDATABASE.userDao().deleteAllUsers()
             startActivity(Intent(this@SettingsMainActivity,LoginActivity::class.java))
             finishAffinity()
         }
        }
    }

    override val layoutView: View
        get() {
            binding = ActivitySettingsMainBinding.inflate(layoutInflater)
            return binding.root
        }
}