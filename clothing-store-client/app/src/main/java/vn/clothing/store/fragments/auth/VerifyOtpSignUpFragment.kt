package vn.clothing.store.fragments.auth

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import vn.clothing.store.R
import vn.clothing.store.databinding.ViewVerifyOtpCodeBinding
import java.lang.StringBuilder

class VerifyOtpSignUpFragment : Fragment(), OnFocusChangeListener, TextView.OnEditorActionListener{
    private var binding:ViewVerifyOtpCodeBinding?=null
    private var otpCode = ""
    private lateinit var bgFilled: Drawable
    private lateinit var bgEmpty: Drawable


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewVerifyOtpCodeBinding.inflate(inflater,container,false)

        init()
        setListenerClick()
        return binding?.root
    }

    private fun init(){
        bgFilled = ContextCompat.getDrawable(requireContext(), R.drawable.bg_otp_active_border)!!
        bgEmpty = ContextCompat.getDrawable(requireContext(), R.drawable.bg_otp_digit)!!
    }


    private fun setListenerClick(){
        if(binding == null) return
        binding!!.edt1.onFocusChangeListener = this
        binding!!.edt2.onFocusChangeListener = this
        binding!!.edt3.onFocusChangeListener = this
        binding!!.edt4.onFocusChangeListener = this
        binding!!.edt5.onFocusChangeListener = this
        binding!!.edt6.onFocusChangeListener = this

        binding!!.edt1.setOnEditorActionListener(this)
        binding!!.edt2.setOnEditorActionListener(this)
        binding!!.edt3.setOnEditorActionListener(this)
        binding!!.edt4.setOnEditorActionListener(this)
        binding!!.edt5.setOnEditorActionListener(this)
        binding!!.edt6.setOnEditorActionListener(this)


        binding!!.edt1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if((p0?.length ?: 0) >= 1){
                    binding!!.edt2.requestFocus()
                    binding!!.edt1.background = bgFilled
                }else{
                    binding!!.edt1.background = bgEmpty
                    if (binding!!.edt1.isFocused) {
                        binding!!.edt1.background = bgFilled
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding!!.edt2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if((p0?.length ?: 0) >= 1){
                    binding!!.edt3.requestFocus()
                    binding!!.edt2.background = bgFilled
                }else{
                    binding!!.edt2.background = bgEmpty
                    if (binding!!.edt2.isFocused) {
                        binding!!.edt2.background = bgFilled
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding!!.edt3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if((p0?.length ?: 0) >= 1){
                    binding!!.edt4.requestFocus()
                    binding!!.edt3.background = bgFilled
                }else{
                    binding!!.edt3.background = bgEmpty
                    if (binding!!.edt3.isFocused) {
                        binding!!.edt3.background = bgFilled
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding!!.edt4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if((p0?.length ?: 0) >= 1){
                    binding!!.edt5.requestFocus()
                    binding!!.edt4.background = bgFilled
                }else{
                    binding!!.edt4.background = bgEmpty
                    if (binding!!.edt4.isFocused) {
                        binding!!.edt4.background = bgFilled
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding!!.edt5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if((p0?.length ?: 0) >= 1){
                    binding!!.edt6.requestFocus()
                    binding!!.edt5.background = bgFilled
                }else{
                    binding!!.edt5.background = bgEmpty
                    if (binding!!.edt5.isFocused) {
                        binding!!.edt5.background = bgFilled
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding!!.edt6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if((p0?.length ?: 0) >= 1){
                    checkOTP()
                }else{
                    binding!!.edt6.background = bgEmpty
                    if (binding!!.edt6.isFocused) {
                        binding!!.edt6.background = bgFilled
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        view?.background = if(hasFocus) bgFilled else bgEmpty
        if (view is EditText){
            if(((view as EditText).text?.length ?: 0) > 1){
                (view as EditText).background = bgFilled
            }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            return checkOTP()
        }
        return false
    }


    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getOTP()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getOTP()
            } else {
                Toast.makeText(requireContext(),getString(R.string.required_permission_notification), Toast.LENGTH_SHORT).show()
            }
        }


    private fun getOTP(){

    }

    private fun checkOTP():Boolean{
        val phone = arguments?.getString("phone")

        val inputOtp = StringBuilder()
            .append(binding!!.edt1.text.toString())
            .append(binding!!.edt2.text.toString())
            .append(binding!!.edt3.text.toString())
            .append(binding!!.edt4.text.toString())
            .append(binding!!.edt5.text.toString())
            .append(binding!!.edt6.text.toString()).toString()
        if(inputOtp == otpCode){
            val result = Bundle().apply {
                putString("phone", phone)
            }
            parentFragmentManager.setFragmentResult("verifyOTP", result)
            parentFragmentManager.popBackStack()
        }else{
            binding!!.edt1.text = null
            binding!!.edt2.text = null
            binding!!.edt3.text = null
            binding!!.edt4.text = null
            binding!!.edt5.text = null
            binding!!.edt6.text = null

            binding!!.edt1.requestFocus()

        }
        return false
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}