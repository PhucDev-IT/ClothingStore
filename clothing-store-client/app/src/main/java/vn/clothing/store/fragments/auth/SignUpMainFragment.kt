package vn.clothing.store.fragments.auth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.clothing.store.R
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.databinding.FragmentSignUpMainBinding
import vn.clothing.store.interfaces.SignUpContract

class SignUpMainFragment : Fragment() , SignUpContract.View{
    private lateinit var _binding:FragmentSignUpMainBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpMainBinding.inflate(inflater,container,false)

        return binding.root
    }


    private fun setListener(){
        binding.btnSignUp.setOnClickListener {

        }

        binding.tvSignIn.setOnClickListener {
            requireActivity().finish()
        }
    }


    //======================================
    // region SignUpContract.View
    //=====================================
    override fun onShowLoading() {

    }

    override fun getContext(): Context {
           return requireActivity()
    }

    override fun onHideLoading() {

    }

    override fun onShowError(message: String?) {

    }

    override fun onShowToast(type: CoreConstant.ToastType, message: String) {

    }

    //======================================
    // endregion SignUpContract.View
    //=====================================
}