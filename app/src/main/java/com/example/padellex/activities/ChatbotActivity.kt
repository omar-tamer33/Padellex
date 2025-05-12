package com.example.padellex.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.padellex.Adapters.ChatsAdapter
import com.example.padellex.Adapters.MessagesAdapter
import com.example.padellex.R
import com.example.padellex.databinding.ActivityChatbotBinding
import com.example.padellex.model.MessageData
import com.example.padellex.utilities.ChatbotManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatbotActivity : AppCompatActivity() {
    @Inject lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityChatbotBinding
    val messages = mutableListOf<MessageData>()
    val bot = ChatbotManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MessagesAdapter(messages,auth.currentUser?.uid!!)
        binding.recyclerView.adapter = adapter

        binding.sendBtn.setOnClickListener{
            val userText = binding.messageEt.text.trim()
            if (userText.isNotEmpty()){
                messages += MessageData(auth.currentUser?.uid!!, userText.toString())
                adapter.notifyItemInserted(messages.lastIndex)
                binding.recyclerView.scrollToPosition(messages.lastIndex)

                val reply = bot.getResponse(userText.toString())
                messages += MessageData(text = reply)
                adapter.notifyItemInserted(messages.lastIndex)
                binding.recyclerView.scrollToPosition(messages.lastIndex)
                binding.messageEt.text.clear()
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

    }
}