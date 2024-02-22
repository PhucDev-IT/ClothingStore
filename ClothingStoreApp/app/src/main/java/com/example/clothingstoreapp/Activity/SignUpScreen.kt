package com.example.clothingstoreapp.Activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.clothingstoreapp.Adapter.CustomDialog
import com.example.clothingstoreapp.MainActivity
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.CustomerService
import com.example.clothingstoreapp.databinding.ActivitySignUpScreenBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SignUpScreen : AppCompatActivity() {
    private lateinit var binding:ActivitySignUpScreenBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var customDialog: CustomDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleApiClient: GoogleApiClient

    private val RC_SIGN_IN:Int = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customDialog = CustomDialog(this)

        db = Firebase.firestore
        mAuth = FirebaseAuth.getInstance()

        handleClick();


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

    }
    private fun handleClick(){
        binding.btnSignUp.setOnClickListener {
            onClickSignUp()
        }

        binding.lnGoogle.setOnClickListener {
            mGoogleApiClient.clearDefaultAccountAndReconnect()
            loginWithGoogle()
        }

        binding.lnApple.setOnClickListener {
            Toast.makeText(this,"Đang bảo trì",Toast.LENGTH_SHORT).show()
        }

        binding.lnFacebook.setOnClickListener {
            Toast.makeText(this,"Đang bảo trì",Toast.LENGTH_SHORT).show()
        }
    }


    private fun onClickSignUp(){
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPass.text.toString().trim()

        if(name.isEmpty()){
            binding.edtName.error = "Vui lòng nhập họ và tên"
        }else if(email.isEmpty()){
            binding.edtName.error = "Email không được để trống"
        }else if(password.isEmpty()){
            binding.edtName.error = "Trường này là bắt buộc"
        }else if(!binding.ckbAgree.isChecked){
            binding.ckbAgree.error = "Vui lòng chấp nhận điều khoản của chúng tôi"
        }else{
            customDialog.dialogLoadingBasic("Đang xử lý....")
            checkIsExistsEmail(name, email, password)
        }
    }

    private fun checkIsExistsEmail(name:String,email:String,password: String){
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task->
            if(task.isSuccessful){
                val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                if (signInMethods.isNotEmpty()) {
                    customDialog.closeDialog()
                    Toast.makeText(this,"Email đã được sử dụng", Toast.LENGTH_LONG).show()
                } else {
                    addNewEmailToFirebase(name,email,password)
                }
            } else {
                customDialog.closeDialog()
                Toast.makeText(this,"Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun addNewEmailToFirebase(name:String,email:String,password: String){
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    //Lấy id ngươời dùng
                    val user = task.result?.user
                    val userId = user?.uid!!

                    mAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        saveInForUser(userId,name,email)
                    }
                        ?.addOnFailureListener {
                            customDialog.closeDialog()
                            Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }else {
                    customDialog.closeDialog()
                    Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                customDialog.closeDialog()
                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveInForUser(id:String,name:String,email: String) {
        val user = hashMapOf(
            "id" to id,
            "name" to name,
            "email" to email
        )
        db.collection("users").document(id).set(user)
            .addOnCompleteListener {
               val intent = Intent(this,SignUpCompleted::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                finishAffinity()
            }.addOnFailureListener {
                customDialog.closeDialog()
                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    //Đăng nhập với google

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
                mAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
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
                Toast.makeText(this,"Không có token",Toast.LENGTH_SHORT).show()
                customDialog.closeDialog()
            }


        } catch (e: ApiException) {

            Log.e(ContentValues.TAG, "Cảnh báo -  signInResult:failed code=" + e.statusCode)
            customDialog.closeDialog()
        }
    }


    private fun updateUI(uid:String){

        getTokenFCM(uid)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    //Đăng ký người dùng bằng tài khoản google
    private fun signInUser(uid:String,name:String?="",email:String?=""){
        val db =  com.google.firebase.Firebase.firestore
        //Check người dùng đã có tài khoản trên firebase chưa

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
                            "email" to email
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
                CustomerService(db).updateTokenFcm(uid,token)
            }
        }

    }
}