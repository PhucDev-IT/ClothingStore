package vn.mobile.clothing.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.mobile.clothing.R
import vn.mobile.clothing.common.CoreConstant
import vn.mobile.clothing.common.PopupDialog
import vn.mobile.clothing.databinding.FragmentSignUpMainBinding
import vn.mobile.clothing.models.User
import vn.mobile.clothing.network.ApiService
import vn.mobile.clothing.network.request.RegisterRequestModel
import vn.mobile.clothing.network.response.ResponseModel
import vn.mobile.clothing.network.rest.BaseCallback


class SignUpMainFragment : Fragment(){
    private lateinit var _binding: FragmentSignUpMainBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpMainBinding.inflate(inflater,container,false)
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
        }else{
           val request = RegisterRequestModel(email,password,fullName)
          requestSignUp(request)
        }
    }

     fun requestSignUp(requeset: RegisterRequestModel) {
       onShowLoading()
        ApiService.APISERVICE.getService().signUp(requeset).enqueue(object :
            BaseCallback<ResponseModel<User>>(){
            override fun onSuccess(model: ResponseModel<User>) {
               onHideLoading()
                if(model.success && model.data!=null){
                   signUpSuccess(model.data!!)
                }else{
                   onShowToast(CoreConstant.ToastType.ERROR,view?.getContext()?.getString(R.string.has_error_please_retry)?:"")
                }
            }

            override fun onError(message: String) {
               onHideLoading()
               onShowError(message)
            }
        })
    }

    //======================================
    // region SignUpContract.View
    //=====================================
     fun onShowLoading() {
        PopupDialog.showDialogLoading(requireActivity())
    }



     fun onHideLoading() {
        PopupDialog.closeDialog()
    }

     fun onShowError(message: String?) {
        PopupDialog.showDialog(requireActivity(), PopupDialog.PopupType.NOTIFICATION,null,message?:getString(R.string.has_error_please_retry)){}
    }

     fun onShowToast(type: CoreConstant.ToastType, message: String) {
        CoreConstant.showToast(requireActivity(),message,type)
    }

     fun signUpSuccess(user: User) {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.container,
            VerifyOtpSignUpFragment()
        ).commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    //======================================
    // endregion SignUpContract.View
    //=====================================
}