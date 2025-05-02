package com.example.padellex.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.padellex.Adapters.ChatsAdapter
import com.example.padellex.activities.ChatActivity
import com.example.padellex.databinding.FragmentChatsBinding
import com.example.padellex.model.UserChatsData
import com.example.padellex.model.UserInfo
import com.example.padellex.viewModels.ChatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatsFragment : Fragment() {
    val viewModel: ChatsViewModel by viewModels()
    lateinit var binding : FragmentChatsBinding
    lateinit var adapter: ChatsAdapter
    val usersList : MutableList<UserChatsData?> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ChatsAdapter(usersList)
        binding.recyclerView.adapter = adapter
        viewModel.getAllChats()
        adapter.onItemClick = {userChatsData->
            navigateToChatActivity(userChatsData)
        }
        observeViewModel()

        binding.addBtn.setOnClickListener {
            val phone = binding.addUserEt.text.toString()
            viewModel.getUserByPhone(phone)
        }
    }

    private fun navigateToChatActivity(userChatsData: UserChatsData){
        val intent = Intent(requireContext(),ChatActivity::class.java)
        intent.putExtra("userChatsData",userChatsData)
        startActivity(intent)
    }

    private fun observeViewModel(){
        viewModel.usersList.observe(viewLifecycleOwner){users->
            if (users != null){
                usersList.clear()
                usersList.addAll(users)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(requireContext(),"No User Found!",Toast.LENGTH_LONG).show()
            }
        }
    }
}