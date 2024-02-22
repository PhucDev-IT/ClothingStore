package com.example.clothingstoreapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvPurchasedHistoryAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.MainActivity
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.PaginationScrollListener
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.Service.OrderService
import com.example.clothingstoreapp.databinding.ActivityPurchasedHistoryScreenBinding
import com.google.android.material.tabs.TabLayout
import java.util.concurrent.atomic.AtomicBoolean


class PurchasedHistoryScreen : AppCompatActivity() {

    private lateinit var binding: ActivityPurchasedHistoryScreenBinding
    private lateinit var adapter: RvPurchasedHistoryAdapter
    private var isLoading: AtomicBoolean = AtomicBoolean(true)
    private var isLastPage: AtomicBoolean = AtomicBoolean(false)
    private lateinit var orderService: OrderService
    private var currentTab: Int = 0
    private var allowBackPress: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasedHistoryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderService = OrderService()

        allowBackPress = intent.getBooleanExtra("allowOnBackPress", true)

        initView()
        handleClick()
        initUI()

    }

    private fun initView() {
        //--------------------- SET UP TAB LAYOUT ---------------------------------
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    currentTab = it.position
                    getFirsData(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Mario Velasco's code
        // Mario Velasco's code
        binding.tabLayout.post {
            val tabLayoutWidth: Int = binding.tabLayout.width
            val metrics = DisplayMetrics()
            this@PurchasedHistoryScreen.windowManager.getDefaultDisplay().getMetrics(metrics)
            val deviceWidth = metrics.widthPixels
            if (tabLayoutWidth < deviceWidth) {
                binding.tabLayout.tabMode = TabLayout.MODE_FIXED
                binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            } else {
                binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
        }

        //----------------- Set up recycler view-------------------
        adapter = RvPurchasedHistoryAdapter(object : ClickObjectInterface<OrderModel> {
            override fun onClickListener(t: OrderModel) {
                val intent = Intent(this@PurchasedHistoryScreen, TrackOrderScreen::class.java)
                intent.putExtra("id", t.id)
                intent.putExtra("status", ProgressOrder.WaitConfirmOrder.name)
                startActivity(intent)
            }
        })

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvOrders.adapter = adapter
        binding.rvOrders.layoutManager = linearLayoutManager

        binding.rvOrders.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItem() {
                isLoading.set(true)
                when(currentTab){
                    0 -> orderService.getNextPage(ProgressOrder.WaitConfirmOrder.name){loadNextPage(it)}
                    1 -> orderService.getNextPage(ProgressOrder.PackagingOrder.name){loadNextPage(it)}
                    2 -> orderService.getNextPage(ProgressOrder.DeliveredOrder.name){loadNextPage(it)}
                    3 -> orderService.getNextPage(ProgressOrder.OrderCanceled.name){loadNextPage(it)}
                }
            }

            override fun isLoading(): Boolean {
                return isLoading.get()
            }

            override fun isLastPage(): Boolean {
                return isLastPage.get()
            }
        })
    }

    private fun initUI() {
        val tab = intent.getIntExtra("key_tab", 0)
        currentTab = tab

        binding.tabLayout.getTabAt(currentTab)?.select()

        getFirsData(tab)
    }

    //---------------- XỬ LÝ LOGIC CHO TỪNG TAB------------------------------------------

    private fun getFirsData(tab: Int) {

        isLastPage.set(false)
        isLoading.set(true)

        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.rvOrders.visibility = View.GONE
        binding.lnChuaCoDonHang.visibility = View.GONE

        when (tab) {
            0 -> orderService.getOrderWaitingConfirm { setDataToView(it) }
            1 -> orderService.getOrderShipping { setDataToView(it) }
            2 -> orderService.getOrderDelivered { setDataToView(it) }
            3 -> orderService.getCancelOrder { setDataToView(it) }
        }


    }

    fun loadNextPage(orders: List<OrderModel>) {
        if (orders.isEmpty()) {
            isLastPage.set(true)
            isLoading.set(true)

            Toast.makeText(this, "Hết rồi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Load tiếp ${orders.size}", Toast.LENGTH_SHORT).show()
            adapter.updateData(orders)
        }
        isLoading.set(false)

    }


    private fun handleClick() {
        binding.swipeRefresh.setOnRefreshListener {
            getFirsData(currentTab)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    private fun setDataToView(order: List<OrderModel>) {

        if (order.isEmpty()) {
            isLoading.set(true)
            isLastPage.set(true)
            binding.lnChuaCoDonHang.visibility = View.VISIBLE
        } else {
            adapter.setData(order)
            binding.rvOrders.visibility = View.VISIBLE
            isLoading.set(false)
        }

        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.swipeRefresh.isRefreshing = false
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (allowBackPress) {
            super.onBackPressed()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

}