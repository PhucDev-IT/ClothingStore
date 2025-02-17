package vn.clothing.store.fragments.auth

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.clothing.store.R
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.FragmentSignUpMainBinding
import vn.clothing.store.interfaces.SignUpContract
import vn.clothing.store.models.User
import vn.clothing.store.networks.request.RegisterRequestModel
import vn.clothing.store.presenter.SignUpPresenter

class SignUpMainFragment : Fragment() , SignUpContract.View{
    private lateinit var _binding:FragmentSignUpMainBinding
    private val binding get() = _binding
    private var presenter:SignUpPresenter?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpMainBinding.inflate(inflater,container,false)

        presenter = SignUpPresenter(this)
        setListener()
        return binding.root
    }


    private fun setListener(){
        binding.btnSignUp.setOnClickListener {
            handleRegister()
        }

        binding.tvSignIn.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun handleRegister(){
        val fullName = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPass.text.toString().trim()

        if(fullName.isBlank()){
            binding.edtName.error = getString(R.string.required_filed)
        }else if(email.isBlank()){
            binding.edtEmail.error = getString(R.string.required_filed)
        }else if(password.isBlank()){
            binding.edtPass.error = getString(R.string.required_filed)
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            CoreConstant.showToast(
                requireContext(),
                "Email không đúng định dạng",
                CoreConstant.ToastType.ERROR
            )
        }
        else if(password.length < 8){
            CoreConstant.showToast(
                requireContext(),
                "Mật khẩu phải có ít nhất 8 ký tự",
                CoreConstant.ToastType.ERROR
            )
        }else if(!binding.ckbAgree.isChecked){
            CoreConstant.showToast(
                requireContext(),
                "Bạn chưa đồng ý với điều khoản",
                CoreConstant.ToastType.ERROR
            )
        }
        else{
           val request = RegisterRequestModel(email,password,fullName)
            presenter?.requestSignUp(request)
        }
    }


    //======================================
    // region SignUpContract.View
    //=====================================
    override fun onShowLoading() {
        PopupDialog.showDialogLoading(requireActivity())
    }

    override fun getContext(): Context {
           return requireActivity()
    }

    override fun onHideLoading() {
        PopupDialog.closeDialog()
    }

    override fun onShowError(message: String?) {
        PopupDialog.showDialog(requireActivity(), PopupDialog.PopupType.NOTIFICATION,null,message?:getString(R.string.has_error_please_retry)){}
    }

    override fun onShowToast(type: CoreConstant.ToastType, message: String) {
        CoreConstant.showToast(requireActivity(),message,type)
    }

    override fun signUpSuccess(user: User) {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.container,VerifyOtpSignUpFragment()).commit()
    }

    override fun onDestroyView() {
        presenter = null
        super.onDestroyView()
    }

    //======================================
    // endregion SignUpContract.View
    //=====================================
}