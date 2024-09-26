package vn.clothing.store.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.clothing.store.R
import vn.clothing.store.activities.order.PurchaseHistoryActivity
import vn.clothing.store.activities.settings.SettingsMainActivity
import vn.clothing.store.adapter.RvDashboardItemViewUser
import vn.clothing.store.common.AppManager
import vn.clothing.store.databinding.FragmentUserBinding
import vn.clothing.store.models.ItemDashboardViewUser

class UserFragment : Fragment() {
    private lateinit var _binding: FragmentUserBinding
    private val binding get() = _binding
    private lateinit var adapter: RvDashboardItemViewUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val items = arrayListOf(
            ItemDashboardViewUser(
                1,
                getString(R.string.purchased_history),
                null,
                R.drawable.icon_order
            ),
            ItemDashboardViewUser(2, getString(R.string.label_coupon), null, R.drawable.discount),
            ItemDashboardViewUser(
                3,
                getString(R.string.label_product_like),
                null,
                R.drawable.icon_order
            ),
            ItemDashboardViewUser(
                4,
                getString(R.string.label_bounty_hunting),
                null,
                R.drawable.icon_order
            ),
        )

        adapter = RvDashboardItemViewUser(items) {
            val intent = Intent(
                requireActivity(), when (it) {
                    1 -> PurchaseHistoryActivity::class.java
                    else -> PurchaseHistoryActivity::class.java
                }
            )

            startActivity(intent)
        }
        binding.rvItem.adapter = adapter

        setListener()
        initUi()
        return binding.root
    }


    private fun initUi() {
        binding.tvName.text = AppManager.user?.fullName?:AppManager.user?.email
    }

    private fun setListener() {
        binding.btnSetting.setOnClickListener {
            startActivity(Intent(requireActivity(), SettingsMainActivity::class.java))
        }
    }
}