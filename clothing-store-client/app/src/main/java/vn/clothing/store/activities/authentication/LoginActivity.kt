package vn.clothing.store.activities.authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.clothing.store.BuildConfig
import vn.clothing.store.MainActivity
import vn.clothing.store.R
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityLoginBinding
import vn.clothing.store.databinding.LayoutPopupBinding
import vn.clothing.store.databinding.PopupDebugBinding
import vn.clothing.store.interfaces.LoginContract
import vn.clothing.store.models.User
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.request.LoginGoogleRequest
import vn.clothing.store.presenter.LoginPresenter
import vn.clothing.store.utils.MySharedPreferences

import vn.clothing.store.utils.Utils
import java.security.MessageDigest
import java.util.Date
import java.util.UUID

class LoginActivity : AppCompatActivity(), LoginContract.View {

    companion object {
        private val TAG = LoginActivity::class.java.name
    }

    private lateinit var binding: ActivityLoginBinding
    private var presenter: LoginPresenter? = null
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

        presenter = LoginPresenter(this)
        setListener()

        if(BuildConfig.DEBUG){
            showDialogDebug()
        }

    }

    private fun setListener() {
        binding.btnSignIn.setOnClickListener {
            checkIsExistsAccount()
        }

        binding.loginWithGoogle.setOnClickListener {
            presenter?.loginWithGoogle()
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
            presenter?.loginSystem(userName, password)
        }
    }


    //===================================================
    //    region HomeContract.View
    //==================================================

    override fun onShowLoading() {
        PopupDialog.showDialogLoading(this)
    }

    override fun onHideLoading() {
        PopupDialog.closeDialog()
    }

    override fun getContext(): Context {
        return this
    }

    override fun onShowError(message: String?) {
        PopupDialog.showDialog(
            this,
            PopupDialog.PopupType.NOTIFICATION,
            null,
            message ?: getString(R.string.has_error_please_retry)
        ) {}
    }

    override fun onShowToast(type: CoreConstant.ToastType, message: String?) {
        CoreConstant.showToast(this, message ?: getString(R.string.has_error_please_retry), type)
    }

    override fun onLoginSuccess(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    //================================================
    //  endregion HomeContract.View
    //===============================================

    private fun showDialogDebug() {
        val url = MySharedPreferences.getStringValues(this,MySharedPreferences.PREF_KEY_URL)
        if(url!=null) return
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        val bindingDialog = PopupDebugBinding.inflate(LayoutInflater.from(this))
        dialog!!.setContentView(bindingDialog.root)

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
        presenter = null
        super.onDestroy()
    }
}