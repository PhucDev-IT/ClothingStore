package vn.clothing.store.activities.order

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.database.models.AddressSchema
import vn.clothing.store.databinding.ActivityPayOrderBinding
import vn.clothing.store.interfaces.PayOrderContract
import vn.clothing.store.presenter.PayOrderPresenter

class PayOrderActivity : BaseActivity(), PayOrderContract.View {
    private lateinit var binding:ActivityPayOrderBinding
    private lateinit var presenter:PayOrderPresenter
    private var address:AddressSchema? = null

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        presenter = PayOrderPresenter(this)
    }

    override fun populateData() {
        presenter.getDefaultAddress()
    }

    override fun setListener() {

    }

    override val layoutView: View
        get() {
            binding = ActivityPayOrderBinding.inflate(layoutInflater)
            return binding.root
        }


    //====================================
    // region PayOrderContract.View
    //=====================================
    override fun onShowLoading() {

    }

    override fun onHideLoading() {

    }

    override fun onShowPopup(message: String, type: PopupDialog.PopupType) {

    }

    override fun onResultAddress(addresses: List<AddressSchema>?) {
        if(!addresses.isNullOrEmpty()){
            binding.btnBuyProduct.isEnabled = true
            for(address in addresses){
                if(address.isDefault) {
                    this.address
                    break
                }
            }
        }else{
            binding.btnBuyProduct.isEnabled = false
            PopupDialog.showDialog(this,PopupDialog.PopupType.CONFIRM,null,getString(R.string.please_add_address)){

            }
        }
    }

    //====================================
    // endregion PayOrderContract.View
    //=====================================

}