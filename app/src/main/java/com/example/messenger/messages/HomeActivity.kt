package com.example.messenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.messenger.R
import com.example.messenger.databinding.ActivityChatBinding
import com.example.messenger.databinding.ActivityHomeBinding
import com.example.messenger.databinding.LatestChatsRowBinding
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.example.messenger.registerlogin.MainActivity
import com.example.messenger.views.LatestChatsRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.viewbinding.BindableItem

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.recyclerViewLatestChats.adapter = adapter
        listenForLatestChats()
        verifyIsUserLoggedIn()
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatActivity::class.java)
            val row = item as LatestChatsRow
            intent. putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
    }

    val latestCharsMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewChats() {
        adapter.clear()
        latestCharsMap.values.forEach{
            adapter.add(LatestChatsRow(it))
        }
    }

    private fun listenForLatestChats() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app").getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestCharsMap[snapshot.key!!] = chatMessage
                adapter.add(LatestChatsRow(chatMessage))
                refreshRecyclerViewChats()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestCharsMap[snapshot.key!!] = chatMessage
                adapter.add(LatestChatsRow(chatMessage))
                refreshRecyclerViewChats()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    val adapter = GroupieAdapter()

    private fun verifyIsUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newMessage -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menuSignOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}