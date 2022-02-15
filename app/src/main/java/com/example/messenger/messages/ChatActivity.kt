package com.example.messenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.messenger.R
import com.example.messenger.databinding.ActivityChatBinding
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.example.messenger.views.ChatFromItem
import com.example.messenger.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupieAdapter

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    val adapter = GroupieAdapter()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        binding.recyclerViewChatLog.adapter = adapter

        listenForMessages()

        binding.sendMessageButton.setOnClickListener {
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null){
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }
                binding.recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun performSendMessage(){
        val text = binding.writeMessageText.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user!!.uid
        val reference = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/user-messages/$fromId/$toId").push()
        if (fromId == null) return

        val toReference = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                binding.writeMessageText.text.clear()
                binding.recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)

        val latestChatsReference = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/latest-messages/$fromId/$toId")
        latestChatsReference.setValue(chatMessage)
        val latestChatsToReference = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/latest-messages/$toId/$fromId")
        latestChatsToReference.setValue(chatMessage)
    }
}
