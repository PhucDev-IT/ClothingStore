package com.example.clothingstoreapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
    private lateinit var binding: ActivityChatDetailsScreenBinding
    private var idChatRoom: String = UserManager.getInstance().getUserID()!!
    private lateinit var adapter: RvMessageAdapter
    private var chatRoomModel: ChatRoomModel = ChatRoomModel()
    private val userCurrent = UserManager.getInstance().getUserCurrent()
    private val PICK_IMAGES_REQUEST = 81


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
        adapter = RvMessageAdapter(applicationContext, options)
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

    private fun handleClick() {
        binding.btnSend.setOnClickListener {
            sendMessageToUser()
        }

        binding.btnPickImage.setOnClickListener {
            openFileChooseImage()
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


    //----------------Xử lý chọn ảnh từ thiết bị cục bộ----------------------


    private fun openFileChooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES_REQUEST)
    }

    @SuppressLint("SuspiciousIndentation")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {

            if (data?.clipData != null) {
                // Multiple images selected
                for (i in 0 until data.clipData!!.itemCount) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    handleImage(uri)
                }
            } else if (data?.data != null) {
                // Single image selected
                val uri = data.data
                handleImage(uri!!)
            }
        }
    }

    private fun handleImage(uri: Uri) {
        // Implement your logic for handling the selected image(s)
    }

}