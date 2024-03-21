package com.example.clothingstoreadmin.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.clothingstoreadmin.MainActivity
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.adapter.CustomDialog
import com.example.clothingstoreadmin.databinding.ActivityLoginScreenBinding
import com.example.clothingstoreadmin.service.CustomerService
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging

class LoginScreen : AppCompatActivity() {
    private lateinit var binding:ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var customDialog: CustomDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleApiClient: GoogleApiClient
    private val customerService = CustomerService()
    private val RC_SIGN_IN:Int = 9001



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        customDialog = CustomDialog(this)


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("473358678062-sa1h1jl8l6d97ch6g6615up8kpk5n3eb.apps.googleusercontent.com") // Thêm scope này
            .requestEmail()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this) {
                // Xử lý lỗi khi không thể kết nối
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        handelEvent();
    }

    private fun handelEvent(){


        binding.btnSignIn.setOnClickListener{
            checkIsExistsAccount()
        }

        binding.lnGoogle.setOnClickListener {
            mGoogleApiClient.clearDefaultAccountAndReconnect()
            loginWithGoogle()
        }

        binding.lnApple.setOnClickListener {
            Toast.makeText(this,"Đang bảo trì", Toast.LENGTH_SHORT).show()
        }

        binding.lnFacebook.setOnClickListener {
            Toast.makeText(this,"Đang bảo trì", Toast.LENGTH_SHORT).show()
        }


    }


    //Check exists account in firebase
    private fun checkIsExistsAccount(){
        val userName = binding.edtTaiKhoan.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if(userName.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Tài khoản hoặc mật khẩu không được để trống", Toast.LENGTH_SHORT).show()
        }else if(password.length <6 || password.length > 20){
            binding.edtPassword.error = "Mật khẩu tối thiểu 6 kí tự và tối đa 20 kí tự"
        }
        else{

            customDialog.dialogLoadingBasic("Đợi một xíu ...")
            auth.signInWithEmailAndPassword(userName,password)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        //Kiểm tra người dùng đã verify email chưa
                        val user = auth.currentUser
                        if ((user != null) && user.isEmailVerified) {
                            updateUI(user.uid)
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
            customDialog.dialogLoadingBasic("Đang xử lý...")
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
                            Log.e(ContentValues.TAG, "Cảnh báo - signInWithCredential:failure", task.exception)
                            customDialog.closeDialog()
                        }
                    }
            }else{
                Toast.makeText(this,"Không có token", Toast.LENGTH_SHORT).show()
                customDialog.closeDialog()
            }


        } catch (e: ApiException) {

            Log.e(ContentValues.TAG, "Cảnh báo -  signInResult:failed code=" + e.statusCode)
            customDialog.closeDialog()
        }
    }


    private fun updateUI(uid:String){

        customerService.getInformationUser(uid){value->
            if(value!=null && value.role == 1){
                getTokenFCM(uid)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }else{
                Toast.makeText(this,"Bạn không có quyền truy cập vào trang quản trị",Toast.LENGTH_SHORT).show()
            }
        }

    }

    //Đăng ký người dùng bằng tài khoản google
    private fun signInUser(uid:String,name:String?="",email:String?=""){

        val userRef = db.collection("users").document(uid)

        userRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        customDialog.closeDialog()
                        updateUI(uid)
                    } else {
                        // Người dùng chưa tồn tại trong Firestore
                        val user = hashMapOf(
                            "id" to uid,
                            "fullName" to name,
                            "email" to email,
                            "role" to 0
                        )
                        userRef.set(user)
                            .addOnCompleteListener {
                                customDialog.closeDialog()
                                updateUI(uid)
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

    //Lấy FCM Token của ứng dụng
    private fun getTokenFCM(uid:String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.w(ContentValues.TAG,"My Token: $token")
                customerService.updateTokenFcm(uid,token)
            }
        }

    }

}