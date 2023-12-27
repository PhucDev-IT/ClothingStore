package com.example.clothingstoreapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clothingstoreapp.Adapter.CustomDialog
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ActivitySignUpScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpScreen : AppCompatActivity() {
    private lateinit var binding:ActivitySignUpScreenBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var customDialog: CustomDialog
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customDialog = CustomDialog(this)

        db = Firebase.firestore
        mAuth = FirebaseAuth.getInstance()

        handleClick();

    }
    private fun handleClick(){
        binding.btnSignUp.setOnClickListener {
            onClickSignUp()

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
            customDialog.dialogBasic("Đang xử lý....")
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

}