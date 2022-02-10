package com.example.messenger.views

import android.view.View
import com.example.messenger.R
import com.example.messenger.databinding.ChatFromUserRowBinding
import com.example.messenger.databinding.ChatToUserRowBinding
import com.example.messenger.models.User
import com.xwray.groupie.viewbinding.BindableItem


class ChatFromItem(val text: String): BindableItem<ChatFromUserRowBinding>() {
    override fun bind(viewBinding: ChatFromUserRowBinding, position: Int) {
        viewBinding.textViewMessage.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_user_row
    }

    override fun initializeViewBinding(view: View): ChatFromUserRowBinding =
        ChatFromUserRowBinding.bind(view)

}

class ChatToItem(val text: String, val user: User): BindableItem<ChatToUserRowBinding>() {
    override fun bind(viewBinding: ChatToUserRowBinding, position: Int) {
        viewBinding.textViewMessage.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_user_row
    }

    override fun initializeViewBinding(view: View): ChatToUserRowBinding =
        ChatToUserRowBinding.bind(view)

}