package com.example.clothingstoreadmin.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.adapter.RvRecentChatAdapter
import com.example.clothingstoreadmin.databinding.ActivityChatScreenBinding
import com.example.clothingstoreadmin.model.ChatRoomModel
import com.example.clothingstoreadmin.service.ChatService

class ChatScreen : AppCompatActivity() {
    private lateinit var binding:ActivityChatScreenBinding
    private lateinit var adapter: RvRecentChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView(){
        adapter = RvRecentChatAdapter(emptyList(),object : ClickObjectInterface<ChatRoomModel>{
            override fun onClickListener(t: ChatRoomModel) {
                val intent = Intent(applicationContext,ChatDetailsScreen::class.java)
                intent.putExtra("informationRoom",t.senderBy)
                startActivity(intent)
            }
        })

        val linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvRecentChat.adapter = adapter
        binding.rvRecentChat.layoutManager = linearLayoutManager

        getData()
    }

    private fun getData(){
        ChatService.allChatroomCollectionReference().addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val list = mutableListOf<ChatRoomModel>()
                for (doc in snapshot.documents) {
                    val mess = doc.toObject(ChatRoomModel::class.java)
                    mess?.idRoom = doc.id
                    if (mess != null) {
                        list.add(mess)
                    }
                }

                adapter.setData(list)
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }
}