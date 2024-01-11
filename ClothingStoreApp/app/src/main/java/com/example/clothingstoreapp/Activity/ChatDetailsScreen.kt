package com.example.clothingstoreapp.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Adapter.RvMessageAdapter
import com.example.clothingstoreapp.Model.ChatRoomModel
import com.example.clothingstoreapp.Model.CustomSender
import com.example.clothingstoreapp.Model.MessageModel
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Service.ChatService
import com.example.clothingstoreapp.databinding.ActivityChatDetailsScreenBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query

class ChatDetailsScreen : AppCompatActivity() {
    private lateinit var binding:ActivityChatDetailsScreenBinding
    private var idChatRoom:String = UserManager.getInstance().getUserID()!!
    private lateinit var adapter: RvMessageAdapter
    private  var chatRoomModel:ChatRoomModel = ChatRoomModel()
    private val userCurrent = UserManager.getInstance().getUserCurrent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupChatRecyclerView()
        handleClick()
    }

    private fun setupChatRecyclerView() {
        val query: Query = ChatService.getChatroomMessageReference(idChatRoom)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<MessageModel> =
            FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel::class.java).build()
        adapter = RvMessageAdapter(applicationContext,options)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        binding.rvMessage.layoutManager = manager
        binding.rvMessage.adapter = adapter
        adapter.startListening()
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvMessage.smoothScrollToPosition(0)
            }
        })
    }

    private fun handleClick(){
        binding.btnSend.setOnClickListener {
            sendMessageToUser()
        }

    }


    private fun sendMessageToUser(){
        if(binding.edtMess.text.toString().trim().isNotEmpty()){
            val sender = CustomSender(userCurrent?.id,userCurrent?.fullName,userCurrent?.avatar,userCurrent?.tokenFCM)
            chatRoomModel.lastMessage = binding.edtMess.text.toString().trim()
            chatRoomModel.lastMessageTimestamp = Timestamp.now()
            chatRoomModel.lastMessageSenderId = userCurrent?.id
            chatRoomModel.senderBy = sender


            ChatService.getChatroomReference(idChatRoom).set(chatRoomModel)

            val messageModel:MessageModel = MessageModel(Timestamp.now(),binding.edtMess.text.toString().trim(),userCurrent?.id)
            ChatService.getChatroomMessageReference(idChatRoom).add(messageModel)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.edtMess.setText("")

                    }
                }
        }
    }
}