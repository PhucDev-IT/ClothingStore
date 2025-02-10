package vn.clothing.store.activities.settings

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.clothing.store.R
import vn.clothing.store.activities.authentication.LoginActivity
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.databinding.ActivitySettingsMainBinding
import vn.clothing.store.utils.MySharedPreferences

class SettingsMainActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsMainBinding

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
            startActivity(Intent(this, AddressActivity::class.java))
        }

        binding.header.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.llSelectColorSystem.setOnClickListener {
            showDialogSelectColorSystem(it)
        }
        binding.btnLogout.setOnClickListener {
            PopupDialog.showDialog(
                this@SettingsMainActivity,
                PopupDialog.PopupType.CONFIRM,
                getString(R.string.app_name),
                getString(R.string.content_logout)
            ) {
                APPDATABASE.clearAllTables()
                startActivity(Intent(this@SettingsMainActivity, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }



    override val layoutView: View
        get() {
            binding = ActivitySettingsMainBinding.inflate(layoutInflater)
            return binding.root
        }

    private fun showDialogSelectColorSystem(anchorView: View) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.dialog_select_color_system, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Lấy các TextView từ giao diện popup
        val itemBright = popupView.findViewById<TextView>(R.id.item_bright)
        val itemDark = popupView.findViewById<TextView>(R.id.item_dark)

        // Xử lý sự kiện chọn chế độ sáng
        itemBright.setOnClickListener {
            changeSystemColorMode(AppCompatDelegate.MODE_NIGHT_NO) // Chế độ sáng
            popupWindow.dismiss() // Đóng popup
        }

        // Xử lý sự kiện chọn chế độ tối
        itemDark.setOnClickListener {
            changeSystemColorMode(AppCompatDelegate.MODE_NIGHT_YES) // Chế độ tối
            popupWindow.dismiss() // Đóng popup
        }

        // Tính toán vị trí hiển thị
        anchorView.post {
            val location = IntArray(2)
            anchorView.getLocationOnScreen(location) // Lấy tọa độ của anchorView

            val anchorWidth = anchorView.width // Độ rộng của anchorView
            val anchorHeight = anchorView.height // Chiều cao của anchorView

            // Lấy kích thước của PopupWindow
            popupView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val popupWidth = popupView.measuredWidth
            val popupHeight = popupView.measuredHeight

            // Tính toán vị trí cuối bên phải
            val xOffset = location[0] + anchorWidth - popupWidth
            val yOffset = location[1] + anchorHeight

            // Hiển thị PopupWindow ở vị trí đã tính toán
            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, xOffset, yOffset+10)
        }
    }
    private fun changeSystemColorMode(mode: Int) {
        MySharedPreferences.setIntValue(this, MySharedPreferences.PREF_KEY_THEME_MODE, mode)
        AppCompatDelegate.setDefaultNightMode(mode)
        recreate() // Khởi động lại Activity để áp dụng thay đổi
    }
}