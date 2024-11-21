package vn.clothing.store.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.activities.order.PayOrderActivity
import vn.clothing.store.adapter.RvItemCartAdapter
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityShoppingCartBinding
import vn.clothing.store.interfaces.ShoppingCartContract
import vn.clothing.store.models.CartModel
import vn.clothing.store.networks.response.CartResponseModel
import vn.clothing.store.presenter.ShoppingCartPresenter
import vn.clothing.store.utils.FormatCurrency

class ShoppingCartActivity : BaseActivity(),ShoppingCartContract.View {

    private lateinit var binding:ActivityShoppingCartBinding
    private var presenter:ShoppingCartPresenter?=null
    private lateinit var adapter:RvItemCartAdapter
    private var cart:CartResponseModel?=null
    private var selectedCart:ArrayList<CartResponseModel.CartItemResponseModel> = arrayListOf()


    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.header.tvName.text = getString(R.string.title_header_cart)
        presenter = ShoppingCartPresenter(this)
        adapter = RvItemCartAdapter(emptyList(),{ checked->
            if(checked.first){
                selectedCart.add(checked.second)
            }else{
                selectedCart.remove(checked.second)
            }
            displayPrice()
        },{ item ->
            val index = selectedCart.indexOf(item)
            if (index != -1) {
                selectedCart[index].quantity  = item.quantity
            }
            displayPrice()
        })
        val dividerItemDecoration = DividerItemDecoration(binding.rvCart.context, DividerItemDecoration.VERTICAL)
        binding.rvCart.adapter = adapter
        binding.rvCart.addItemDecoration(dividerItemDecoration)
        binding.rvCart.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    }

    override fun populateData() {
    }

    override fun setListener() {
        binding.btnBuyProduct.setOnClickListener {
            if(selectedCart.isEmpty()){
                CoreConstant.showToast(this,getString(R.string.please_select_item),CoreConstant.ToastType.WARNING)
                return@setOnClickListener
            }
            val intent =Intent(this,PayOrderActivity::class.java)
            intent.putExtra(IntentData.KEY_LIST_CART_ITEM,selectedCart)
            startActivity(intent)
        }

        binding.header.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityShoppingCartBinding.inflate(layoutInflater)
            return binding.root
        }

    /**
     * Whenever change item, we will calculate price
     */
    private fun displayPrice(){
        var price:Double = 0.0
        for(item in selectedCart){
            price += item.price!! * item.quantity!!
        }
        binding.tvSumMoney.text = FormatCurrency.numberFormat.format(price)
    }

    //===================================================
    //    region ShoppingCartContract.View
    //==================================================

    override fun onShowLoading() {
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    override fun onHiddenLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
    }

    override fun showError(message: String?) {
        PopupDialog.showDialog(this,PopupDialog.PopupType.NOTIFICATION,null,message?:getString(R.string.has_error_please_retry)){}
    }

    override fun onResultCarts(cart: CartResponseModel?) {
        if(cart == null || cart.listItem.isNullOrEmpty()){
            binding.lnNotCart.visibility = View.VISIBLE
            binding.rvCart.visibility = View.GONE
        }else{
            binding.lnNotCart.visibility = View.GONE
            binding.rvCart.visibility = View.VISIBLE
            adapter.setData(cart.listItem)
            this.cart = cart
        }
    }

    //================================================
    //  endregion ShoppingCartContract.View
    //===============================================

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        presenter?.getAllCarts()
    }
}
