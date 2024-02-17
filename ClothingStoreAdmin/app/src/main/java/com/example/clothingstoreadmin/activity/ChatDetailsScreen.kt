package com.example.clothingstoreadmin.activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.bumptech.glide.Glide
import com.example.clothingstoreadmin.adapter.RvMessageAdapter
import com.example.clothingstoreadmin.api.ApiNotificationService
import com.example.clothingstoreadmin.databinding.ActivityChatDetailsScreenBinding
import com.example.clothingstoreadmin.model.ChatRoomModel
import com.example.clothingstoreadmin.model.CustomSender
import com.example.clothingstoreadmin.model.Customer
import com.example.clothingstoreadmin.model.Data
import com.example.clothingstoreadmin.model.DataMessageNotification
import com.example.clothingstoreadmin.model.MessageModel
import com.example.clothingstoreadmin.model.Product
import com.example.clothingstoreadmin.model.UserManager
import com.example.clothingstoreadmin.service.ChatService
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatDetailsScreen : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailsScreenBinding
    private lateinit var adapter: RvMessageAdapter
    private var chatRoomModel: ChatRoomModel = ChatRoomModel()
    private val userCurrent = UserManager.getInstance().getUserCurrent()
    private var idChatRoom: String = ""
    private lateinit var userChat:CustomSender

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
        userChat  = intent.getSerializableExtra("informationRoom") as CustomSender

        binding.tvFullName.text = userChat.fullName
        if(userChat.avatar!=null){
            Glide.with(this).load(userChat.avatar).into(binding.imgAvatar)
        }

            idChatRoom = userChat.id.toString()
            setupChatRecyclerView()

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

        binding.btnAddImage.setOnClickListener {

        }

    }


    private fun sendMessageToUser() {
        if (binding.edtMess.text.toString().trim().isNotEmpty()) {

            chatRoomModel.lastMessage = binding.edtMess.text.toString().trim()
            chatRoomModel.lastMessageTimestamp = Timestamp.now()
            chatRoomModel.lastMessageSenderId = userCurrent?.id

            ChatService.getChatroomReference(idChatRoom).set(chatRoomModel)

            val messageModel: MessageModel = MessageModel(
                Timestamp.now(),
                binding.edtMess.text.toString().trim(),
                userCurrent?.id
            )
            ChatService.getChatroomMessageReference(idChatRoom).add(messageModel)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendNotification()
                        binding.edtMess.setText("")

                    }
                }
        }
    }

    private fun sendNotification(){
        val api = ApiNotificationService.create()
        userChat.tokenFCM?.let {
            api.sendNotification(
                DataMessageNotification(it,
                    Data("Tin nhắn mới từ Clothing Store", binding.edtMess.text.toString().trim())
                )
            )
                .enqueue(object : Callback<DataMessageNotification> {
                    override fun onResponse(
                        call: Call<DataMessageNotification>?,
                        response: Response<DataMessageNotification>?
                    ) {
                        if(response?.isSuccessful == true){
                            Toast.makeText(application,"Gửi thành công", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(application,"Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DataMessageNotification>?, t: Throwable?) {
                        Toast.makeText(application,t?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
        }


    }
        

}