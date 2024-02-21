package com.example.clothingstoreapp.FragmentLayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Activity.TrackOrderScreen
import com.example.clothingstoreapp.Adapter.RvPurchasedHistoryAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.PaginationScrollListener
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.ViewModel.PurchaseHistoryViewModel
import com.example.clothingstoreapp.databinding.FragmentWaitingConfirmOrderBinding


class WaitingConfirmOrderFragment : Fragment() {
    private lateinit var _binding:FragmentWaitingConfirmOrderBinding
    private val binding get() = _binding
    private lateinit var adapter: RvPurchasedHistoryAdapter
    private var  isLoading:Boolean = false
    private var isLastPage:Boolean = false

    private val sharedViewModel: PurchaseHistoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaitingConfirmOrderBinding.inflate(inflater,container,false)
        initView()
        handleClick()
        return binding.root
    }

    private fun initView(){
        adapter = RvPurchasedHistoryAdapter(emptyList(),object : ClickObjectInterface<OrderModel>{
            override fun onClickListener(t: OrderModel) {
                val intent = Intent(context,TrackOrderScreen::class.java)
                intent.putExtra("id",t.id)
                intent.putExtra("status",ProgressOrder.WaitConfirmOrder.name)
                startActivity(intent)
            }
        })

        val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
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

        sharedViewModel.getOrderWaitingConfirm {
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