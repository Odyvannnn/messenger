package com.example.messenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.messenger.R
import com.example.messenger.databinding.ActivityNewMessageBinding
import com.example.messenger.databinding.UserRowNewMessageBinding
import com.example.messenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.viewbinding.BindableItem

class NewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Выберете пользователя"

        fetchUsers()

    }
    companion object{
        const val USER_KEY = "USER_KEY"
    }


    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupieAdapter()

                p0.children.forEach{
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context,ChatActivity::class.java )
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                binding.recyclerViewNewMessage.setAdapter(adapter)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: User) : BindableItem<UserRowNewMessageBinding>() {

    override fun bind(viewBinding: UserRowNewMessageBinding, position: Int) {
        viewBinding.usernameNewMessage.text = user.username
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun initializeViewBinding(view: View): UserRowNewMessageBinding =
        UserRowNewMessageBinding.bind(view)

}
