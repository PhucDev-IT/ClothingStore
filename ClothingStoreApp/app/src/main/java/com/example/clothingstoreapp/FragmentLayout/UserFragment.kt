package com.example.clothingstoreapp.FragmentLayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clothingstoreapp.Activity.LoginScreen
import com.example.clothingstoreapp.Activity.MyVoucherScreen
import com.example.clothingstoreapp.Activity.ProductsIsLikedScreen
import com.example.clothingstoreapp.Activity.PurchasedHistoryScreen
import com.example.clothingstoreapp.Activity.SettingsActivity
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.FragmentUserBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class UserFragment : Fragment(), View.OnClickListener {
    private lateinit var _binding: FragmentUserBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        handleClick()

        initView()
        return binding.root
    }

    private fun initView() {
        binding.tvFullName.text =
            com.example.clothingstoreapp.Model.UserManager.getInstance().getUserCurrent()?.fullName
    }

    private fun handleClick() {

        binding.lnThietLapTaiKhoan.setOnClickListener(this)
        binding.btnSetting.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
        binding.lnLichSuMuaHang.setOnClickListener(this)
        binding.lnMyCoupon.setOnClickListener(this)
        binding.lnProductIsLiked.setOnClickListener(this)
        binding.lnDangXuLy.setOnClickListener(this)
        binding.lnPreview.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        var intent: Intent? = null
        when (v?.id) {
            R.id.btnSetting, R.id.lnThietLapTaiKhoan -> {
                intent = Intent(context, SettingsActivity::class.java)
            }

            R.id.lnLichSuMuaHang -> {
                intent = Intent(context, PurchasedHistoryScreen::class.java)
                intent.putExtra("key_tab","Purchase")
            }

            R.id.lnMyCoupon -> {
                intent = Intent(context, MyVoucherScreen::class.java)

            }

            R.id.lnProductIsLiked -> {
                intent = Intent(context, ProductsIsLikedScreen::class.java)

            }

            R.id.lnDangXuLy ->{
                intent = Intent(context, PurchasedHistoryScreen::class.java)
                intent.putExtra("key_tab","Processing")
            }

            R.id.lnPreview->{
                intent = Intent(context, PurchasedHistoryScreen::class.java)
                intent.putExtra("key_tab","isPreview")
            }

            R.id.btnLogout -> {
                Firebase.auth.signOut()
                intent = Intent(context, LoginScreen::class.java)
                requireActivity().finishAffinity()
            }

        }

        intent?.let { startActivity(it) }
    }
}