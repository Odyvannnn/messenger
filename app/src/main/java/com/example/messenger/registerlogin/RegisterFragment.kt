package com.example.messenger.registerlogin

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.messenger.R
import com.example.messenger.databinding.FragmentRegisterBinding
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.registerButton.setOnClickListener {
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()
            createAccount(email, password)
        }

        binding.goToLogin.setOnClickListener {
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.loginFragment)
        }

        //Firebase Auth
        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload();
        }
    }

    private fun reload() {
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.emailRegister.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.emailRegister.error = "Required."
            valid = false
        } else {
            binding.emailRegister.error = null
        }

        val password = binding.passwordRegister.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.passwordRegister.error = "Required."
            valid = false
        } else {
            binding.passwordRegister.error = null
        }

        return valid
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        context, "Пользователь создан.",
                        Toast.LENGTH_SHORT
                    ).show()
                    saveUserToFirebaseDatabase()
                    updateUI(user)
                    val navController = NavHostFragment.findNavController(this)
                    navController.navigate(R.id.action_registerFragment_to_homeActivity)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Ошибка регистрации.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid  ?: ""
        val ref = FirebaseDatabase.getInstance("https://messenger-36423-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/users/$uid")
        val user = User(uid, binding.usernameRegister.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterFragment", "User saved to database")
            }
    }
}
