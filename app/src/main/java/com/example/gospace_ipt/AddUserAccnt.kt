package com.example.gospace_ipt

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gospace_ipt.databinding.ActivityAddUserAccntBinding
import com.example.gospace_ipt.databinding.ActivityAdminRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddUserAccnt : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserAccntBinding
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserAccntBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()

        // -----Loading-----------
        val progressBar = findViewById<ImageView>(R.id.animatedProgressBar)
        Glide.with(this)
            .asGif()
            .load(R.drawable.ic_loading)
            .into(progressBar)

        if (!isNetworkAvailable()) {
            Toast.makeText(
                this,
                "No internet connection detected. Some features may not work.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Back button
        binding.back.setOnClickListener {
            val back = Intent(this, AdProfileFragment::class.java)
            startActivity(back)
            finish()
        }

        //------------------ Register account---------------------------
        binding.btnRegister.setOnClickListener {
            showProgressBar()
            val email = binding.email.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPass = binding.ConPassword.text.toString()
            val name = binding.name.text.toString()
            val gender = binding.genderC.selectedItem.toString()
            val role = binding.roleSpinner.selectedItem.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty() && role.isNotEmpty()) {
                if (password == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = firebaseAuth.currentUser?.uid
                                val user = User(email, password, name, gender, role)
                                if (userId != null) {
                                    database.child(userId).setValue(user)
                                        .addOnCompleteListener { dbTask ->
                                            hideProgressBar()
                                            if (dbTask.isSuccessful) {
                                                Toast.makeText(this@AddUserAccnt, "Account registered successfully.", Toast.LENGTH_SHORT).show()
                                                startActivity(Intent(this@AddUserAccnt, AdminSignUp::class.java))
                                                finish()
                                            } else {
                                                Toast.makeText(this@AddUserAccnt, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    hideProgressBar()
                                    Toast.makeText(this@AddUserAccnt, "Failed to retrieve user ID.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                hideProgressBar()
                                Toast.makeText(this@AddUserAccnt, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    hideProgressBar()
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            } else {
                hideProgressBar()
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    private fun showProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.GONE
    }
}
