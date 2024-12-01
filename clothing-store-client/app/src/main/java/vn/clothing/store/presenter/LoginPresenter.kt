package vn.clothing.store.presenter

import android.util.Log
import android.widget.Toast
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
import kotlinx.coroutines.withContext
import vn.clothing.store.R
import vn.clothing.store.activities.authentication.LoginActivity
import vn.clothing.store.activities.authentication.LoginActivity.Companion
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.interfaces.LoginContract
import vn.clothing.store.models.User
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.request.LoginGoogleRequest
import vn.clothing.store.networks.request.LoginRequest
import vn.clothing.store.networks.response.LoginResponseModel
import vn.clothing.store.utils.MySharedPreferences
import vn.clothing.store.utils.Utils
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback
import java.security.MessageDigest
import java.util.Date
import java.util.UUID

class LoginPresenter(private var view: LoginContract.View?) : LoginContract.Presenter {

    companion object {
        private val TAG = LoginPresenter::class.java.name
        private const val WEB_CLIENT_ID = "473358678062-l7ic9nrjhvfh1j6ecmfluaoqkurs4bqe.apps.googleusercontent.com"
    }


    override fun loginWithGoogle() {
        if (view == null) {
            throw NullPointerException("Context is null")
        }
        val credentialManager = CredentialManager.create(view!!.getContext())

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val nonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .setNonce(nonce)
            .build()

        val request: androidx.credentials.GetCredentialRequest =
            androidx.credentials.GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = credentialManager.getCredential(view!!.getContext(), request)
                handleSignIn(result)
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    view?.onShowToast(CoreConstant.ToastType.ERROR,view?.getContext()?.getString(R.string.has_error_please_retry)?:"")
                }
                e.printStackTrace()
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val data = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        Utils.getTokenFCM { token->
                            val login = LoginGoogleRequest(data.id,"",token,"",data.displayName,data.familyName,data.givenName,data.phoneNumber,
                                data.profilePictureUri.toString()
                            )
                            requestLoginGoogle(login)
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                }
            }

            is PublicKeyCredential -> {
                val response = credential.authenticationResponseJson
                Log.w(TAG, "PublicKeyCredential = $response")
            }

            // Password credential
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                Log.w(TAG, "username = $username, password = $password")
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    override fun loginSystem(email: String, password: String) {
        view?.onShowLoading()
        Utils.getTokenFCM { token->
            val login = LoginRequest(email,password,token,UUID.randomUUID().toString())
            APISERVICE.getService().login(login).enqueue(object: BaseCallback<ResponseModel<LoginResponseModel>>(){
                override fun onSuccess(model: ResponseModel<LoginResponseModel>) {

                    if(!model.success || model.data==null){
                        view?.onHideLoading()
                        view?.onShowToast(CoreConstant.ToastType.ERROR,model.error?.message)
                    }
                    else{
                        CoroutineScope(Dispatchers.IO).launch {
                            AppManager.user = model.data!!.user!!
                            AppManager.token = model.data?.token
                            MySharedPreferences.setStringValue(view?.getContext()!!,MySharedPreferences.PREF_TOKEN,model.data?.token!!)
                            APPDATABASE.userDao().upsertUser(AppManager.user!!)
                            withContext(Dispatchers.Main){
                                view?.onHideLoading()
                                view?.onLoginSuccess(model.data?.user!!)
                            }
                        }
                    }
                }
                override fun onError(message: String) {
                    view?.onHideLoading()
                    view?.onShowError(message)
                }
            })
        }

    }




    private fun requestLoginGoogle(login:LoginGoogleRequest){
        view?.onShowLoading()
            APISERVICE.getService().loginGoogle(login).enqueue(object: BaseCallback<ResponseModel<User>>() {
                override fun onSuccess(model: ResponseModel<User>) {
                    view?.onHideLoading()

                }

                override fun onError(message: String) {
                    view?.onHideLoading()
                    view?.onShowError(message)
                }
            })

    }
}