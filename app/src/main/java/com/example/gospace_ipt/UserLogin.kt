package com.example.gospace_ipt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gospace_ipt.databinding.ActivityUserLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserLogin : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")


        // -----Loading-----------
        val progressBar = findViewById<ImageView>(R.id.animatedProgressBar)
        Glide.with(this)
            .asGif()
            .load(R.drawable.ic_loading)
            .into(progressBar)



        binding.back.setOnClickListener {
            val back = Intent(this, ChooseUser::class.java)
            startActivity(back)
        }


        binding.loginBtn.setOnClickListener {
            val email = binding.username.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                showProgressBar()

                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser = firebaseAuth.currentUser
                            if (currentUser != null) {
                                val userId = currentUser.uid
                                database.child(userId).get().addOnCompleteListener { roleTask ->
                                    if (roleTask.isSuccessful) {
                                        val userRole = roleTask.result?.child("role")?.value.toString()

                                        // Check if role is Admin Root or GSO
                                        if (userRole == "Admin Root" || userRole == "GSO") {
                                            Toast.makeText(
                                                this,
                                                "You are not authorized to login as Admin or GSO.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            hideProgressBar()
                                        } else {
                                            val toUserPage = Intent(this, UserViewerPage::class.java)
                                            startActivity(toUserPage)
                                            hideProgressBar()
                                        }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Failed to retrieve user role.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        hideProgressBar()
                                    }
                                }
                            } else {
                                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                                hideProgressBar()
                            }
                        } else {
                            Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                            hideProgressBar()
                        }
                    }

            } else {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
        }

    }


    private fun showProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.GONE
    }
}