package com.example.messenger.views

import android.view.View
import com.example.messenger.R
import com.example.messenger.databinding.LatestChatsRowBinding
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.viewbinding.BindableItem

class LatestChatsRow(val chatMessage: ChatMessage): BindableItem<LatestChatsRowBinding>() {
    var chatPartnerUser: User? = null
    override fun bind(viewBinding: LatestChatsRowBinding, position: Int) {
        viewBinding.latestChatMessage.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app").getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewBinding.latestChatUsername.text = chatPartnerUser?.username
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_chats_row
    }

    override fun initializeViewBinding(view: View): LatestChatsRowBinding =
        LatestChatsRowBinding.bind(view)

}