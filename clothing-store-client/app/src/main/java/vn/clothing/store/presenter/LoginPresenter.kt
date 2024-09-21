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
import vn.clothing.store.R
import vn.clothing.store.interfaces.LoginContract
import java.security.MessageDigest
import java.util.Date
import java.util.UUID

class LoginPresenter(private var view: LoginContract.View?) : LoginContract.Presenter {

    companion object {
        private val TAG = LoginPresenter::class.java.name
        private const val WEB_CLIENT_ID = ""
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

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val result = credentialManager.getCredential(view!!.getContext(), request)
                handleSignIn(result)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(TAG, "Error = ${e.message}")
            }
        }
    }


    override fun loginSystem(userName: String, password: String) {
        view?.onShowLoading()


    }


    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        googleIdTokenCredential.


                    } catch (e: GoogleIdTokenParsingException) {
                        view!!.onShowError("Received an invalid google id token response ${e.message}")
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
}