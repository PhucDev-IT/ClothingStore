package vn.clothing.store.activities.settings

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityAddressBinding
import vn.clothing.store.databinding.AddNewDeliveryInfoBinding
import vn.clothing.store.fragments.AddNewDeliveryInfoFragment
import vn.clothing.store.interfaces.NewDeliveryInfoContract
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.presenter.NewDeliveryInfoPresenter

class UpdateAddressActivity: BaseActivity(), NewDeliveryInfoContract.View  {
    private lateinit var binding: AddNewDeliveryInfoBinding
    private lateinit var address: DeliveryInformation
    private var preseneter : NewDeliveryInfoPresenter? = null


    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(!intent.hasExtra(IntentData.KEY_ADDRESS)) finish()

        address = intent.getSerializableExtra(IntentData.KEY_ADDRESS) as DeliveryInformation
        preseneter = NewDeliveryInfoPresenter(this)
        val dividerItemDecoration = DividerItemDecoration(binding.rvAddress.context, DividerItemDecoration.VERTICAL)

        binding.rvAddress.adapter = preseneter?.adapter
        binding.rvAddress.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        binding.rvAddress.addItemDecoration(dividerItemDecoration)
        binding.header.tvName.text = getString(R.string.header_update_address)

    }

    override fun populateData() {
       binding.edtFullName.setText(address.fullName)
        binding.edtNumberPhone.setText(address.numberPhone)
        extractInfo()
    }

    override fun setListener() {
        binding.tvThietLapLai.setOnClickListener {
            preseneter?.resetData()
            binding.tvCurrentName.text = "Tỉnh/ Thành phố"
            binding.lnProvince.visibility = View.GONE
            binding.lnDistrict.visibility = View.GONE
            binding.lnWard.visibility = View.GONE
            binding.tvCurrentName.visibility = View.VISIBLE
        }

        binding.btnFinish.setOnClickListener {
            if(binding.edtFullName.text.toString().isEmpty()){
                binding.edtFullName.error = getString(R.string.required_filed)
            }else if(binding.edtNumberPhone.text.toString().isEmpty()){
                binding.edtNumberPhone.error =  getString(R.string.required_filed)
            }else if(preseneter?.checkData() == false){
                CoreConstant.showToast(this,"Vui lòng chọn địa chỉ",CoreConstant.ToastType.ERROR)
            }else{
                preseneter?.updateAddress(address.id,binding.edtFullName.text.toString().trim(),binding.edtNumberPhone.text.toString().trim())
            }
        }

        binding.header.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override val layoutView: View
        get() {
            binding = AddNewDeliveryInfoBinding.inflate(layoutInflater)
            return binding.root
        }


    private fun extractInfo(){
        val infos = address.details.split(",")
        onSelectedProvinces(infos[0])
        onSelectedDistrict(infos[1])
        onSelectedWard(infos[2])
    }


    //=================================================
    //  region Listener
    //==============================================
    override fun showError(error: String) {
        PopupDialog.showDialog(this,
            PopupDialog.PopupType.NOTIFICATION,getString(R.string.can_not_success),error){}
    }

    override fun onShowLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    override fun onHideLoading() {
        binding.progressbar.visibility = View.GONE
        PopupDialog.closeDialog()
    }

    override fun onSelectedProvinces(name:String){
        binding.tvCurrentName.text = "Quận/Huyện"
        binding.tvProvince.text = name
        binding.lnProvince.visibility = View.VISIBLE
    }

    override  fun onSelectedDistrict(name:String){
        binding.tvCurrentName.text = "Phường/Xã"
        binding.tvDistrict.text = name
        binding.lnDistrict.visibility = View.VISIBLE
    }

    override  fun onSelectedWard(name:String){
        binding.tvWard.text = name
        binding.lnWard.visibility = View.VISIBLE
        preseneter?.adapter?.setData(null)
        binding.tvCurrentName.visibility = View.GONE
    }

    override fun onShowDialogLoading() {
        PopupDialog.showDialogLoading(this)
    }

    override fun onAddSuccess() {

    }

    override fun onUpdateSuccess(address: DeliveryInformation) {
        super.onUpdateSuccess(address)
        CoreConstant.showToast(this,getString(R.string.success),CoreConstant.ToastType.SUCCESS)
        finish()
    }

    //=================================================
    // endregion
    //===============================================
}