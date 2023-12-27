package com.example.clothingstoreapp.Activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.Adapter.CustomDialog
import com.example.clothingstoreapp.MainActivity
import com.example.clothingstoreapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore


class LoginScreen : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var customDialog: CustomDialog

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleApiClient:GoogleApiClient

    private val RC_SIGN_IN:Int = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        customDialog = CustomDialog(this)


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("473358678062-sa1h1jl8l6d97ch6g6615up8kpk5n3eb.apps.googleusercontent.com") // Thêm scope này
            .requestEmail()
            .build()

         mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this) { connectionResult ->
                // Xử lý lỗi khi không thể kết nối
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        handelEvent();
    }

    private fun handelEvent(){
        binding.tvRegister.setOnClickListener{
            val intent = Intent(this, SignUpScreen::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener{
            checkIsExistsAccount()
        }

        binding.lnGoogle.setOnClickListener {
            mGoogleApiClient.clearDefaultAccountAndReconnect()
            loginWithGoogle()
        }


    }


    //Check exists account in firebase
    private fun checkIsExistsAccount(){
        val userName = binding.edtTaiKhoan.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if(userName.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Tài khoản hoặc mật khẩu không được để trống",Toast.LENGTH_SHORT).show();
        }else{

            customDialog.dialogBasic("Đợi một xíu ...")
             auth.signInWithEmailAndPassword(userName,password)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        //Kiểm tra người dùng đã verify email chưa
                        val user = auth.currentUser
                        if ((user != null) && user.isEmailVerified) {
                                updateUI()
                        }else{
                            Toast.makeText(this, "Vui lòng xác minh Email của bạn !", Toast.LENGTH_LONG)
                                .show()
                            customDialog.closeDialog()
                        }
                    }else{
                        Toast.makeText(this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT)
                            .show()
                        customDialog.closeDialog()
                    }
                } .addOnFailureListener {
                    Toast.makeText(this, "Có lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                     customDialog.closeDialog()
                }


        }
    }


    //Login with goole
    private fun loginWithGoogle(){

        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent,RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            customDialog.dialogBasic("Đang xử lý...")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken

            if(idToken!=null){
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if(user!=null)
                            {
                                val userName = account.displayName // Tên người dùng
                                val userEmail = account.email // Email
                                signInUser(user.uid,userName,userEmail)
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "Cảnh báo - signInWithCredential:failure", task.exception)
                            customDialog.closeDialog()
                        }
                    }
            }else{
                Toast.makeText(this,"Không có token",Toast.LENGTH_SHORT).show()
                customDialog.closeDialog()
            }


        } catch (e: ApiException) {

            Log.e(TAG, "Cảnh báo -  signInResult:failed code=" + e.statusCode)
            customDialog.closeDialog()
        }
    }


    private fun updateUI(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    //Đăng ký người dùng bằng tài khoản google
    private fun signInUser(uid:String,name:String?="",email:String?=""){
        val db =  Firebase.firestore
        //Check người dùng đã có tài khoản trên firebase chưa

        val userRef = db.collection("users").document(uid)

        userRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        updateUI()
                    } else {
                        // Người dùng chưa tồn tại trong Firestore
                        val user = hashMapOf(
                            "id" to uid,
                            "fullName" to name,
                            "email" to email
                        )
                        userRef.set(user)
                            .addOnCompleteListener {
                                updateUI()
                            }.addOnFailureListener {
                                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                                customDialog.closeDialog()
                            }
                    }
                } else {
                    // Xử lý lỗi khi truy cập Firestore
                    Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    customDialog.closeDialog()
                }
            }
    }
}