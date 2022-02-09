package com.example.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.messenger.databinding.ActivityNewMessageBinding

class NewMessageActivity : AppCompatActivity() {

    private var _binding: ActivityNewMessageBinding? = null
    private val binding: ActivityNewMessageBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)


        supportActionBar?.title = "Выберете пользователя"

        binding.recyclerViewNewMessage.adapter
    }
}