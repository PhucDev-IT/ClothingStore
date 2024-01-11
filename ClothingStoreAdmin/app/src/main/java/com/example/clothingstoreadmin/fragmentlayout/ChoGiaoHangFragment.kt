package com.example.clothingstoreadmin.fragmentlayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.activity.OrderDetailsScreen
import com.example.clothingstoreadmin.adapter.RvOrderAdapter
import com.example.clothingstoreadmin.databinding.FragmentChoGiaoHangBinding
import com.example.clothingstoreadmin.model.OrderModel
import com.example.clothingstoreadmin.model.PaginationScrollListener
import com.example.clothingstoreadmin.service.OrderService


class ChoGiaoHangFragment : Fragment() {
    private lateinit var _binding: FragmentChoGiaoHangBinding
    private val binding get() = _binding
    private lateinit var adapter: RvOrderAdapter
    private var  isLoading:Boolean = false
    private var isLastPage:Boolean = false
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
        adapter = RvOrderAdapter(emptyList(),object : ClickObjectInterface<OrderModel> {
            override fun onClickListener(t: OrderModel) {
                val intent = Intent(context,OrderDetailsScreen::class.java)
                intent.putExtra("order",t)
                startActivity(intent)
            }
        })

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        binding.rvOrders.adapter = adapter
        binding.rvOrders.layoutManager = linearLayoutManager

        binding.rvOrders.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager){
            override fun loadMoreItem() {

            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }
        })

        getFirsData()
    }

    private fun getFirsData(){
        isLoading  = true

        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility =  View.VISIBLE
        binding.rvOrders.visibility = View.GONE
        binding.lnChuaCoDonHang.visibility = View.GONE

        orderService.getOrderWaitShipping {
            isLoading = false

            if(it.isEmpty()){
                isLoading = true
                isLastPage  = true
                binding.lnChuaCoDonHang.visibility = View.VISIBLE
            }else{
                adapter.setData(it)
                binding.rvOrders.visibility = View.VISIBLE
            }

            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.swipeRefresh.isRefreshing  = false
        }

    }

    private fun handleClick(){
        binding.swipeRefresh.setOnRefreshListener {
            getFirsData()
        }
    }

}