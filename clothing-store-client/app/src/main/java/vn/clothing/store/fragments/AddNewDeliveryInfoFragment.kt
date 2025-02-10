package vn.clothing.store.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import vn.clothing.store.R
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.AddNewDeliveryInfoBinding

import vn.clothing.store.interfaces.NewDeliveryInfoContract
import vn.clothing.store.presenter.NewDeliveryInfoPresenter

class AddNewDeliveryInfoFragment : Fragment(), NewDeliveryInfoContract.View {
   private lateinit var _binding:AddNewDeliveryInfoBinding
    private val binding get() = _binding
    private var preseneter : NewDeliveryInfoPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddNewDeliveryInfoBinding.inflate(layoutInflater)
        preseneter = NewDeliveryInfoPresenter(this)
        initView()
        return binding.root
    }

    private fun initView(){
        val dividerItemDecoration = DividerItemDecoration(binding.rvAddress.context, DividerItemDecoration.VERTICAL)

        binding.rvAddress.adapter = preseneter?.adapter
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvAddress.addItemDecoration(dividerItemDecoration)
        preseneter?.getProvinces()

        binding.header.tvName.text = getString(R.string.header_add_new_address)
        setListener()
    }


    private fun setListener(){
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
            }else{
                preseneter?.addNewDeliveryInfo(binding.edtFullName.text.toString(),binding.edtNumberPhone.text.toString())
            }
        }

        binding.header.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    //=================================================
    //  region Listener
    //==============================================
    override fun showError(error: String) {
        PopupDialog.showDialog(requireContext(),PopupDialog.PopupType.NOTIFICATION,getString(R.string.can_not_success),error){}
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
        PopupDialog.showDialogLoading(requireContext())
    }

    override fun onAddSuccess() {
        CoreConstant.showToast(requireContext(),"Thêm thành công",CoreConstant.ToastType.SUCCESS)
        parentFragmentManager.popBackStack()
    }

    //=================================================
    // endregion
    //===============================================

}