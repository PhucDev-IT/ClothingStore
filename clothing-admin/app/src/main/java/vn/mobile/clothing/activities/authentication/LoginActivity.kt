package vn.mobile.clothing.activities.authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.mobile.clothing.BuildConfig
import vn.mobile.clothing.MainActivity
import vn.mobile.clothing.R
import vn.mobile.clothing.common.AppManager
import vn.mobile.clothing.common.CoreConstant
import vn.mobile.clothing.common.PopupDialog
import vn.mobile.clothing.database.AppDatabase.Companion.APPDATABASE
import vn.mobile.clothing.databinding.ActivityLoginBinding
import vn.mobile.clothing.databinding.PopupDebugBinding
import vn.mobile.clothing.models.User
import vn.mobile.clothing.network.ApiService.Companion.APISERVICE
import vn.mobile.clothing.network.request.LoginRequest
import vn.mobile.clothing.network.response.LoginResponseModel
import vn.mobile.clothing.network.response.ResponseModel
import vn.mobile.clothing.network.rest.BaseCallback
import vn.mobile.clothing.utils.MySharedPreferences
import vn.mobile.clothing.utils.Utils


class LoginActivity : AppCompatActivity(){

    companion object {
        private val TAG = LoginActivity::class.java.name
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setListener()

        if(BuildConfig.DEBUG){
           // showDialogDebug()
        }

    }

    private fun setListener() {
        binding.btnSignIn.setOnClickListener {
            checkIsExistsAccount()
        }

        binding.loginWithGoogle.setOnClickListener {

        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    //Check exists account in firebase
    private fun checkIsExistsAccount() {
        val userName = binding.edtTaiKhoan.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (userName.isEmpty() || password.isEmpty()) {
            CoreConstant.showToast(
                this,
                getString(R.string.account_empty),
                CoreConstant.ToastType.ERROR
            )
        } else {
            loginSystem(userName, password)
        }
    }


    private fun loginSystem(email: String, password: String) {
        onShowLoading()
        Utils.getTokenFCM { token->
            val login = LoginRequest(email,password,token,"1")
            APISERVICE.getService().login(login).enqueue(object: BaseCallback<ResponseModel<LoginResponseModel>>(){
                override fun onSuccess(model: ResponseModel<LoginResponseModel>) {

                    if(!model.success || model.data==null){
                        onHideLoading()
                        onShowToast(CoreConstant.ToastType.ERROR,model.error?.message)
                    }
                    else{
                        CoroutineScope(Dispatchers.IO).launch {
                            AppManager.user = model.data!!.user!!
                            AppManager.token = model.data?.token
                            MySharedPreferences.setStringValue(this@LoginActivity,MySharedPreferences.PREF_TOKEN,model.data?.token!!)
                            APPDATABASE.userDao().upsertUser(AppManager.user!!)
                            withContext(Dispatchers.Main){
                                onHideLoading()
                                onLoginSuccess(model.data?.user!!)
                            }
                        }
                    }
                }
                override fun onError(message: String) {
                    onHideLoading()
                    onShowError(message)
                }
            })
        }

    }


    //===================================================
    //    region HomeContract.View
    //==================================================

     fun onShowLoading() {
        PopupDialog.showDialogLoading(this)
    }

     fun onHideLoading() {
        PopupDialog.closeDialog()
    }

     fun onShowError(message: String?) {
        PopupDialog.showDialog(
            this,
            PopupDialog.PopupType.NOTIFICATION,
            null,
            message ?: getString(R.string.has_error_please_retry)
        ) {}
    }

     fun onShowToast(type: CoreConstant.ToastType, message: String?) {
        CoreConstant.showToast(this, message ?: getString(R.string.has_error_please_retry), type)
    }

     fun onLoginSuccess(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    //================================================
    //  endregion HomeContract.View
    //===============================================

    private fun showDialogDebug() {
        val url = MySharedPreferences.getStringValues(this, MySharedPreferences.PREF_KEY_URL)
        if(url!=null) return
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        val bindingDialog = PopupDebugBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(bindingDialog.root)

        bindingDialog.btnConfirm.setOnClickListener {
            if(bindingDialog.edtUrl.text.toString().isEmpty()){
               bindingDialog.edtUrl.error = "Vui lòng nhập URL"
                return@setOnClickListener
            }
            APISERVICE.setBaseUrl(bindingDialog.edtUrl.text.toString())
            MySharedPreferences.setStringValue(this,MySharedPreferences.PREF_KEY_URL,bindingDialog.edtUrl.text.toString())
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}