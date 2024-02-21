package com.example.clothingstoreapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.clothingstoreapp.Adapter.RvPurchasedHistoryAdapter
import com.example.clothingstoreapp.Adapter.ViewPagerPurchasedAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
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
    private var  isLoading: AtomicBoolean = AtomicBoolean(true)
    private var isLastPage: AtomicBoolean = AtomicBoolean(false)
    private lateinit var orderService: OrderService
    private var currentTab:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasedHistoryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderService = OrderService()
        initView()
        handleClick()
        initUI()

    }

    private fun initView(){
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
        adapter = RvPurchasedHistoryAdapter(emptyList(),object : ClickObjectInterface<OrderModel> {
            override fun onClickListener(t: OrderModel) {
                val intent = Intent(this@PurchasedHistoryScreen,TrackOrderScreen::class.java)
                intent.putExtra("id",t.id)
                intent.putExtra("status", ProgressOrder.WaitConfirmOrder.name)
                startActivity(intent)
            }
        })

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rvOrders.adapter = adapter
        binding.rvOrders.layoutManager = linearLayoutManager

        binding.rvOrders.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager){
            override fun loadMoreItem() {

            }

            override fun isLoading(): Boolean {
                return isLoading.get()
            }

            override fun isLastPage(): Boolean {
                return isLastPage.get()
            }
        })

        getFirsData(currentTab)
    }

    private fun initUI() {
        val tab = intent.getStringExtra("key_tab")

        getFirsData(0)
    }

    //---------------- XỬ LÝ LOGIC CHO TỪNG TAB------------------------------------------

    private fun getFirsData(tab:Int){

        isLoading.set(true)

        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility =  View.VISIBLE
        binding.rvOrders.visibility = View.GONE
        binding.lnChuaCoDonHang.visibility = View.GONE

        when(tab){
            0 -> orderService.getOrderWaitingConfirm{setDataToView(it)}
            1 -> orderService.getOrderShipping{setDataToView(it)}
            2 -> orderService.getOrderDelivered{setDataToView(it)}
        }


    }

//    fun loadNextPage(){
//        isLoading.set(true) // Gán giá trị true cho biến isLoading
//
//        orderService.getNextPage(){
//            if(it.isEmpty()){
//                isLastPage.set(true)
//                isLoading.set(true)
//
//                Toast.makeText(this,"Hết rồi", Toast.LENGTH_SHORT).show()
//            }else{
//                Toast.makeText(this,"Load tiếp ${it.size}", Toast.LENGTH_SHORT).show()
//                adapter.updateData(it)
//            }
//            isLoading.set(false)
//        }
//    }


    private fun handleClick(){
        binding.swipeRefresh.setOnRefreshListener {
            getFirsData(0)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }



    private fun setDataToView(order:List<OrderModel>){
        isLoading.set(false)

        if(order.isEmpty()){
            isLoading.set(true)
            isLastPage.set(true)
            binding.lnChuaCoDonHang.visibility = View.VISIBLE
        }else{
            adapter.setData(order)
            binding.rvOrders.visibility = View.VISIBLE
        }

        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.swipeRefresh.isRefreshing  = false
    }

}