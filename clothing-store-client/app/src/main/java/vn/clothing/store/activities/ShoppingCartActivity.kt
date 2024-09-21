package vn.clothing.store.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.activities.order.PayOrderActivity
import vn.clothing.store.adapter.RvItemCartAdapter
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityShoppingCartBinding
import vn.clothing.store.interfaces.ShoppingCartContract
import vn.clothing.store.networks.response.CartResponseModel

class ShoppingCartActivity : BaseActivity(),ShoppingCartContract.View {

    private lateinit var binding:ActivityShoppingCartBinding
    private var presenter:ShoppingCartContract.Presenter?=null
    private lateinit var adapter:RvItemCartAdapter

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = RvItemCartAdapter(emptyList(),{},{})
    }

    override fun populateData() {

    }

    override fun setListener() {
        binding.btnBuyProduct.setOnClickListener {
            val intent =Intent(this,PayOrderActivity::class.java)
            startActivity(intent)
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityShoppingCartBinding.inflate(layoutInflater)
            return binding.root
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

    override fun onLoadedData(data: CartResponseModel) {
        adapter.setData(data.listItem)
    }

    override fun showError(message: String?) {
        PopupDialog.showDialog(this,PopupDialog.PopupType.NOTIFICATION,null,message?:getString(R.string.has_error_please_retry)){}
    }

    //================================================
    //  endregion ShoppingCartContract.View
    //===============================================
}
