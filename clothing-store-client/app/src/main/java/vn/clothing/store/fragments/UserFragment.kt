package vn.clothing.store.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import vn.clothing.store.R
import vn.clothing.store.activities.MyVoucherActivity
import vn.clothing.store.activities.authentication.LoginActivity
import vn.clothing.store.activities.order.PurchaseHistoryActivity
import vn.clothing.store.activities.settings.SettingsMainActivity
import vn.clothing.store.adapter.RvDashboardItemViewUser
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.databinding.FragmentUserBinding
import vn.clothing.store.models.ItemDashboardViewUser
import vn.clothing.store.networks.ApiService
import vn.clothing.store.networks.response.StatisticalCommonResModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

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
                    2-> MyVoucherActivity::class.java
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

        binding.btnLogout.setOnClickListener {
            PopupDialog.showDialog(requireContext(),
                PopupDialog.PopupType.CONFIRM,getString(R.string.app_name),getString(R.string.content_logout)){
                APPDATABASE.clearAllTables()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finishAffinity()
            }
        }
    }

    private fun setUpChart(model:StatisticalCommonResModel){
        binding.chart.setUsePercentValues(false)
        binding.chart.description.isEnabled = false
        binding.chart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.chart.dragDecelerationFrictionCoef = 0.99f

        binding.chart.isDrawHoleEnabled = true
        binding.chart.setHoleColor(R.color.white)
        binding.chart.holeRadius = 58f
        binding.chart.transparentCircleRadius = 61f

        binding.chart.animateY(1000,Easing.EaseInOutQuad)

        val yVals = arrayListOf<PieEntry>()
        yVals.add(PieEntry(model.total, "Thanh toán"))
        yVals.add(PieEntry(model.discount, "Mã giảm giá"))

        val dataSet = PieDataSet(yVals,"Thống kê")

        dataSet.sliceSpace = 2f
        dataSet.selectionShift = 5f
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS,255)

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(10f)
        pieData.setValueTextColor(R.color.colorPrimary)

        binding.chart.data = pieData
        binding.chart.invalidate()

    }


    private fun statistical(){
        ApiService.APISERVICE.getService(AppManager.token).statisticalCommon(AppManager.user?.id?:"").enqueue(object: BaseCallback<ResponseModel<StatisticalCommonResModel>>(){
            override fun onSuccess(model: ResponseModel<StatisticalCommonResModel>) {
                if(model.success && model.data!=null){
                    setUpChart(model.data!!)
                }
            }

            override fun onError(message: String) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        statistical()
    }
}