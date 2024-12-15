package vn.mobile.clothing

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import vn.mobile.clothing.activities.AddVoucherActivity
import vn.mobile.clothing.activities.OrderActivity
import vn.mobile.clothing.activities.authentication.LoginActivity
import vn.mobile.clothing.activities.base.BaseActivity
import vn.mobile.clothing.common.PopupDialog
import vn.mobile.clothing.database.AppDatabase.Companion.APPDATABASE
import vn.mobile.clothing.databinding.ActivityMainBinding
import vn.mobile.clothing.databinding.PopupDebugBinding
import vn.mobile.clothing.fragments.DashboardFragment
import vn.mobile.clothing.network.ApiService.Companion.APISERVICE
import vn.mobile.clothing.utils.MySharedPreferences

class MainActivity : BaseActivity() , NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
            binding.navView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun populateData() {
        if(BuildConfig.DEBUG){
        //    checkData()
        }
    }

    override fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityMainBinding.inflate(layoutInflater)
            return binding.root
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(DashboardFragment())
            R.id.nav_order -> {
                startActivity(Intent(this@MainActivity,OrderActivity::class.java))
                return false
            }
            R.id.nav_settings -> {
              //  val intent = Intent(this,SettingsActivity::class.java)
              //  startActivity(intent)
            }

            R.id.nav_voucher->{
                startActivity(Intent(this@MainActivity,AddVoucherActivity::class.java))
                return false
            }

            R.id.nav_notification -> {
//                val intent = Intent(this,NotificationActivity::class.java)
//                startActivity(intent)
            }

            R.id.nav_logout -> {
                PopupDialog.showDialog(this,
                    PopupDialog.PopupType.CONFIRM,getString(R.string.app_name),getString(R.string.content_logout)){
                    APPDATABASE.userDao().deleteAllUsers()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("CommitTransaction")
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null)
            .commit()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen) {
            binding.drawerLayout.close()
        } else {
            super.onBackPressed()
        }
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