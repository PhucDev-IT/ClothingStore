package vn.clothing.store.activities.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.clothing.store.R
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityLoginBinding
import vn.clothing.store.interfaces.LoginContract
import vn.clothing.store.presenter.LoginPresenter
import java.security.MessageDigest
import java.util.Date
import java.util.UUID

class LoginActivity : AppCompatActivity() , LoginContract.View{

    companion object{
        private val TAG = LoginActivity::class.java.name
    }

    private lateinit var binding:ActivityLoginBinding
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
    }

    private fun setListener(){
        binding.btnSignIn.setOnClickListener {
            checkIsExistsAccount()
        }

        binding.loginWithGoogle.setOnClickListener {
            presenter?.loginWithGoogle()
        }

        binding.tvRegister.setOnClickListener {
           val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    //Check exists account in firebase
    private fun checkIsExistsAccount(){
        val userName = binding.edtTaiKhoan.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if(userName.isEmpty() || password.isEmpty()){
            CoreConstant.showToast(this,getString(R.string.account_empty),CoreConstant.ToastType.ERROR)
        }else{
            presenter?.loginSystem(userName,password)
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
        PopupDialog.showDialog(this, PopupDialog.PopupType.NOTIFICATION,null,message?:getString(R.string.has_error_please_retry)){}
    }

    override fun onShowToast(type: CoreConstant.ToastType, message: String) {

    }

    //================================================
    //  endregion HomeContract.View
    //===============================================


    override fun onDestroy() {
        presenter = null
        super.onDestroy()
    }
}