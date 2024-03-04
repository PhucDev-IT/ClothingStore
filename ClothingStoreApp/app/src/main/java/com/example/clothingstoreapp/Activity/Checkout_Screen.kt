package com.example.clothingstoreapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.clothingstoreapp.Model.CustomProduct
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.ViewModel.PayOrderViewModel

class Checkout_Screen : AppCompatActivity() {

    private val viewModel : PayOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out_screen)

        val list = intent.getSerializableExtra("listCart") as? ArrayList<ItemCart>
        if (list != null) {
            viewModel.setListCart(list.toList())
        }
    }
}