package com.example.padellex.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.padellex.Adapters.MessagesAdapter
import com.example.padellex.databinding.ActivityChatBinding
import com.example.padellex.model.MessageData
import com.example.padellex.model.UserChatsData
import com.example.padellex.model.UserInfo
import com.example.padellex.viewModels.ChatsViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    @Inject lateinit var auth: FirebaseAuth
    val viewModel : ChatsViewModel by viewModels()
    var userChatsData: UserChatsData? = null
    lateinit var binding : ActivityChatBinding
    lateinit var adapter : MessagesAdapter
    val messagesList = mutableListOf<MessageData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MessagesAdapter(messagesList , auth.currentUser!!.uid)
        binding.recyclerView.adapter = adapter
        userChatsData = intent.getParcelableExtra("userChatsData")
        viewModel.getAllMessage(userChatsData!!.chatId)
        observeViewModel()
        binding.userNameTv.text = "${userChatsData?.firstName} ${userChatsData?.lastName}"

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.sendBtn.setOnClickListener{
            val text = binding.messageEt.text.toString().trim()
            if (text.isNotBlank()) {
                lifecycleScope.launch {
                    val senderInfo = viewModel.getUserById(auth.currentUser!!.uid)
                    val receiverId = userChatsData!!.participants.first { it != senderInfo!!.id }
                    if (senderInfo != null) {
                        viewModel.sendMessage(receiverId, text , senderData = UserChatsData(
                            userChatsData!!.chatId,
                            userChatsData!!.participants,
                            userChatsData!!.lastMessage, userChatsData!!.timestamp,
                            firstName = senderInfo.firstName,
                            lastName = senderInfo.lastName,
                            imageUrl = senderInfo.imageUrl),
                            receiverData = userChatsData!!)
                    }
                }
                binding.messageEt.text.clear()
            }
        }

    }

    private fun observeViewModel(){
        viewModel.messagesList.observe(this){listOfMessages->
            messagesList.clear()
            messagesList.addAll(listOfMessages)
            adapter.notifyDataSetChanged()
            binding.recyclerView.scrollToPosition(messagesList.size - 1)
        }
    }
}