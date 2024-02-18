package com.example.clothingstoreadmin.fragmentlayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.activity.OrderDetailsScreen
import com.example.clothingstoreadmin.adapter.RvOrderAdapter
import com.example.clothingstoreadmin.databinding.FragmentChoGiaoHangBinding
import com.example.clothingstoreadmin.model.OrderModel
import com.example.clothingstoreadmin.model.PaginationScrollListener
import com.example.clothingstoreadmin.model.ProgressOrder
import com.example.clothingstoreadmin.service.OrderService
import java.util.concurrent.atomic.AtomicBoolean


class ChoGiaoHangFragment : Fragment() {
    private lateinit var _binding: FragmentChoGiaoHangBinding
    private val binding get() = _binding
    private lateinit var adapter: RvOrderAdapter
    private var  isLoading:AtomicBoolean = AtomicBoolean(true)
    private var isLastPage:AtomicBoolean = AtomicBoolean(false)
    private val orderService = OrderService()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoGiaoHangBinding.inflate(inflater,container,false)
        initView()
        handleClick()
        return binding.root
    }
    private fun initView(){
        adapter = RvOrderAdapter(object : ClickObjectInterface<OrderModel> {
            override fun onClickListener(t: OrderModel) {
                val intent = Intent(context,OrderDetailsScreen::class.java)
                intent.putExtra("order",t)
                startActivity(intent)
            }
        })

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        binding.rvOrders.adapter = adapter
        binding.rvOrders.layoutManager = linearLayoutManager

        getFirsData()

        binding.rvOrders.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager){
            override fun loadMoreItem() {
                loadNextPage()
            }

            override fun isLoading(): Boolean {
                return isLoading.get()
            }

            override fun isLastPage(): Boolean {
                return isLastPage.get()
            }
        })


    }

    fun loadNextPage(){
        isLoading.set(true) // Gán giá trị true cho biến isLoading

        orderService.getNextPage(ProgressOrder.TransportingOrder.name){
            if(it.isEmpty()){
                isLastPage.set(true)
                isLoading.set(true)

                Toast.makeText(context,"Hết rồi",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Load tiếp ${it.size}",Toast.LENGTH_SHORT).show()
                adapter.updateData(it)
            }
            isLoading.set(false)
        }
    }
    private fun getFirsData(){
        isLoading.set(true)
        isLastPage.set(false)

        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility =  View.VISIBLE
        binding.rvOrders.visibility = View.GONE
        binding.lnChuaCoDonHang.visibility = View.GONE

        orderService.getOrderWaitShipping {
            if(it.isEmpty()){
                isLoading.set(true)
                isLastPage.set(true)
                binding.lnChuaCoDonHang.visibility = View.VISIBLE
            }else{
                adapter.setData(it)
                binding.rvOrders.visibility = View.VISIBLE
                isLoading.set(false)
            }

            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.swipeRefresh.isRefreshing  = false


        }

    }

    private fun handleClick(){
        binding.swipeRefresh.setOnRefreshListener {
            orderService.lastIdOrder = null
            getFirsData()
        }
    }

}