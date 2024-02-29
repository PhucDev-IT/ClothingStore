package com.example.clothingstoreapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvItemCartAdapter
import com.example.clothingstoreapp.Interface.CkbObjectInterface
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.CartService
import com.example.clothingstoreapp.ViewModel.PayOrderViewModel
import com.example.clothingstoreapp.databinding.ActivityMyCartScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MyCartScreen : AppCompatActivity() {
    private lateinit var binding:ActivityMyCartScreenBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: RvItemCartAdapter
    private lateinit var cartService: CartService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCartScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        cartService = CartService(db)
        initCart()

        onClick()
    }

    private fun onClick(){
        binding.swipRefresh.setOnRefreshListener {
            Handler().postDelayed({
                getFirstData()
                binding.swipRefresh.isRefreshing = false
            }, 2000)
        }

        binding.btnBuyProduct.setOnClickListener {
            if(selectedCart.size <=0){
                Toast.makeText(this,"Chọn sản phẩm cần mua để tiếp tục thực hiện", Toast.LENGTH_SHORT).show()
            }else{

               val intent = Intent(this,Checkout_Screen::class.java)
                intent.putExtra("listCart",ArrayList(selectedCart))
                startActivity(intent)
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    private var selectedCart = mutableListOf<ItemCart>()
    private fun initCart(){

        adapter = RvItemCartAdapter(emptyList(),object : ClickObjectInterface<ItemCart> {
            override fun onClickListener(t: ItemCart) {
                for(item in selectedCart){
                    if(item.idCart == t.idCart){
                        item.product?.quantity = t.product?.quantity
                        break
                    }
                }
                caculatorMoney()
            }
        }, object : CkbObjectInterface<ItemCart> {
            override fun isChecked(m: ItemCart) {
                selectedCart.add(m)
                caculatorMoney()
            }

            override fun isNotChecked(m: ItemCart) {
                selectedCart.remove(m)
                caculatorMoney()
            }
        })

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rvCart.adapter = adapter
        binding.rvCart.layoutManager = linearLayoutManager

        getFirstData()
    }

    //Tính toán tiền
    private fun caculatorMoney(){
        var sum:Double = 0.0
        for(item in selectedCart){
            sum += (item.product?.quantity?.times(item.product?.price!!)!!)
        }
        binding.tvSumMoney.text = FormatCurrency.numberFormat.format(sum)
    }

    private fun getFirstData(){
        val uid = UserManager.getInstance().getUserID()
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility =  View.VISIBLE

        if (uid != null) {
            cartService.selectData(uid){list->
                if(list.isEmpty()){
                    binding.lnNotCart.visibility = View.VISIBLE
                    binding.rvCart.visibility = View.GONE
                }else{
                    adapter.setData(list)

                    binding.lnNotCart.visibility = View.GONE
                    binding.rvCart.visibility = View.VISIBLE
                }
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
            }
        }
    }

}