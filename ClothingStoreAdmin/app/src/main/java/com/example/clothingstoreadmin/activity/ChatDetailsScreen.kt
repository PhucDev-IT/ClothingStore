package com.example.clothingstoreadmin.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.bumptech.glide.Glide
import com.example.clothingstoreadmin.adapter.RvMessageAdapter
import com.example.clothingstoreadmin.databinding.ActivityChatDetailsScreenBinding
import com.example.clothingstoreadmin.model.ChatRoomModel
import com.example.clothingstoreadmin.model.CustomSender
import com.example.clothingstoreadmin.model.MessageModel
import com.example.clothingstoreadmin.model.UserManager
import com.example.clothingstoreadmin.service.ChatService
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class ChatDetailsScreen : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailsScreenBinding
    private lateinit var adapter: RvMessageAdapter
    private var chatRoomModel: ChatRoomModel = ChatRoomModel()
    private val userCurrent = UserManager.getInstance().getUserCurrent()
    private var idChatRoom: String = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        handleClick()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initView() {
        val room: ChatRoomModel? = intent.getSerializableExtra("room",ChatRoomModel::class.java)

        binding.tvFullName.text = room?.senderBy?.fullName
        if(room?.senderBy?.avatar!=null){
            Glide.with(this).load(room.senderBy!!.avatar).into(binding.imgAvatar)
        }

        if (room?.idRoom != null) {
            idChatRoom = room.idRoom!!
            setupChatRecyclerView()
        }
    }

    private fun setupChatRecyclerView() {
        val query: Query = ChatService.getChatroomMessageReference(idChatRoom)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<MessageModel> =
            FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel::class.java).build()
        adapter = RvMessageAdapter(applicationContext, options)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        binding.rvMessage.layoutManager = manager
        binding.rvMessage.adapter = adapter
        adapter.startListening()
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvMessage.smoothScrollToPosition(0)
            }
        })
    }

    private fun handleClick() {
        binding.btnSend.setOnClickListener {
            sendMessageToUser()
        }

    }


    private fun sendMessageToUser() {
        if (binding.edtMess.text.toString().trim().isNotEmpty()) {
            val sender = CustomSender(
                userCurrent?.id,
                userCurrent?.fullName,
                userCurrent?.avatar,
                userCurrent?.tokenFCM
            )
            chatRoomModel.lastMessage = binding.edtMess.text.toString().trim()
            chatRoomModel.lastMessageTimestamp = Timestamp.now()
            chatRoomModel.lastMessageSenderId = userCurrent?.id
            chatRoomModel.senderBy = sender


            ChatService.getChatroomReference(idChatRoom).set(chatRoomModel)

            val messageModel: MessageModel = MessageModel(
                Timestamp.now(),
                binding.edtMess.text.toString().trim(),
                userCurrent?.id
            )
            ChatService.getChatroomMessageReference(idChatRoom).add(messageModel)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.edtMess.setText("")

                    }
                }
        }
    }

}