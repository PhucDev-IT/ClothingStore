package vn.mobile.clothing.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import vn.mobile.clothing.R
import vn.mobile.clothing.activities.base.BaseActivity
import vn.mobile.clothing.adapters.RvPurchasedHistoryAdapter
import vn.mobile.clothing.common.IntentData
import vn.mobile.clothing.databinding.ActivityMainBinding
import vn.mobile.clothing.databinding.ActivityOrderBinding
import vn.mobile.clothing.models.EOrderStatus
import vn.mobile.clothing.viewmodel.OrderViewModel

class OrderActivity : BaseActivity() {
    private lateinit var binding:ActivityOrderBinding
    private var currentTab:String  = "PENDING"
    private var indexTab:Int = 0
    private lateinit var viewModel:OrderViewModel
    private lateinit var adapter: RvPurchasedHistoryAdapter

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpUi()
        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        adapter = RvPurchasedHistoryAdapter{order->
            val intent = Intent(this,TrackOrderActivity::class.java)
            intent.putExtra(IntentData.KEY_ORDER,order.orderId)
            startActivity(intent)
        }

        binding.rvOrders.adapter = adapter
        binding.rvOrders.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        val dividerItemDecoration = DividerItemDecoration(binding.rvOrders.context, DividerItemDecoration.VERTICAL)
        binding.rvOrders.addItemDecoration(dividerItemDecoration)
    }

    override fun populateData() {
        viewModel.getFirstOrder(currentTab)
    }

    override fun setListener() {
        binding.header.toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.orders.observe(this@OrderActivity){orders->
            orders?.let { adapter.setData(it) }
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityOrderBinding.inflate(layoutInflater)
            return binding.root
        }


    private fun setUpUi(){

        //--------------------- SET UP TAB LAYOUT ---------------------------------
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    indexTab = it.position
                    currentTab = when (it.position) {
                        0 ->  "PENDING"
                        1 -> "PACKING"
                        2 -> EOrderStatus.SHIPPING.name
                        3 -> EOrderStatus.DELIVERED.name
                        else -> "PENDING"
                    }
                }
                adapter.clearData()
                viewModel.getFirstOrder(currentTab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Mario Velasco's code
        // Mario Velasco's code
        binding.tabLayout.post {
            val tabLayoutWidth: Int = binding.tabLayout.width
            val metrics = DisplayMetrics()
            this@OrderActivity.windowManager.getDefaultDisplay().getMetrics(metrics)
            val deviceWidth = metrics.widthPixels
            if (tabLayoutWidth < deviceWidth) {
                binding.tabLayout.tabMode = TabLayout.MODE_FIXED
                binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            } else {
                binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
        }

        binding.tabLayout.getTabAt(0)?.select()
    }

}