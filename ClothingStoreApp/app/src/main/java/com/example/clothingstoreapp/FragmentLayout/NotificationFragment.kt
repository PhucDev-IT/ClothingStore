package com.example.clothingstoreapp.FragmentLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvNotificationAdapter
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.NotificationService
import com.example.clothingstoreapp.databinding.FragmentNotificationBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class NotificationFragment : Fragment() {
   private lateinit var _binding:FragmentNotificationBinding
    private val binding get() = _binding
    private lateinit var adapterForMe:RvNotificationAdapter
    private lateinit var adapterAll:RvNotificationAdapter
    private lateinit var notificationService: NotificationService
    private lateinit var db:FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater,container,false)

        db = Firebase.firestore
        notificationService = NotificationService(db)
        initView()
        return binding.root
    }


    private fun initView(){
        val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        notificationService.getNotificationByID { list->
            if(list.isNotEmpty()){
                binding.lnNotificationForMe.visibility = View.VISIBLE
                adapterForMe = RvNotificationAdapter(list)
                binding.rvNotificationForMe.adapter = adapterForMe
                binding.rvNotificationForMe.layoutManager = linearLayoutManager
            }
        }

        notificationService.getAllNotification { list->
            if(list.isNotEmpty()){
                binding.lnNotificationAll.visibility = View.VISIBLE
                adapterAll = RvNotificationAdapter(emptyList())

                binding.rvNotificationAll.adapter = adapterAll
                binding.rvNotificationAll.layoutManager = linearLayoutManager
            }
        }


    }



}