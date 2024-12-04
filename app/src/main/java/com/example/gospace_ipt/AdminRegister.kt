package com.example.gospace_ipt

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gospace_ipt.databinding.ActivityAdminRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminRegister : AppCompatActivity() {

    private lateinit var binding: ActivityAdminRegisterBinding
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminRegisterBinding.inflate(layoutInflater)
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

        binding.back.setOnClickListener {
            val back = Intent(this, AdminSignUp::class.java)
            startActivity(back)
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
                    Log.d("AdminRegister", role)

                    // Check if role is Admin Root or GSO before allowing registration
                    if (role == "Admin Root" || role == "GSO") {
                        checkUserRole(role) { exists ->
                            if (exists) {
                                hideProgressBar()
                                Toast.makeText(
                                    this@AdminRegister,
                                    "Role already exists. Only one account can exist for the $role role.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Add a delay before continuing registration
                                Handler().postDelayed({
                                    createAccountInDatabase(email, password, name, gender, role)
                                }, 1000) // Delay of 1 second
                            }
                        }
                    } else {
                        createAccountInDatabase(email, password, name, gender, role)
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

    private fun createAccountInDatabase(email: String, password: String, name: String, gender: String, role: String) {
        val userId = database.push().key // Generate a new key for the user
        val user = User(email, password, name, gender, role)

        if (userId != null) {
            database.child(userId).setValue(user)
                .addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        // After the database insertion succeeds, create the Firebase Authentication user
                        createAccountInAuth(email, password, name, gender, role, userId)
                    } else {
                        hideProgressBar()
                        Toast.makeText(this@AdminRegister, "Failed to save user data. $role may be taken already. Only 1 account per Admin Root and GSO only", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    hideProgressBar()
                    Toast.makeText(this@AdminRegister, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            hideProgressBar()
            Toast.makeText(this@AdminRegister, "Error: Unable to create unique user ID.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccountInAuth(email: String, password: String, name: String, gender: String, role: String, userId: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(email, password, name, gender, role)
                    val firebaseUserId = firebaseAuth.currentUser?.uid
                    if (firebaseUserId != null) {
                        database.child(firebaseUserId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                hideProgressBar()
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this@AdminRegister, "Account registered successfully.", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@AdminRegister, AdminSignUp::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this@AdminRegister, "Failed to save user data. $role may be taken already. Only 1 account per Admin Root and GSO only", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener { exception ->
                                hideProgressBar()
                                Toast.makeText(this@AdminRegister, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        hideProgressBar()
                        Toast.makeText(this@AdminRegister, "Failed to retrieve Firebase User ID.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(this@AdminRegister, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun checkUserRole(role: String, callback: (Boolean) -> Unit) {
        // Get reference to your Firebase Realtime Database
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

        // Query the user's role
        database.orderByChild("role").equalTo(role).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if a user with the same role already exists
                if (snapshot.exists()) {
                    callback(true) // Role already exists
                } else {
                    callback(false) // Role is available for new user
                }
            }

            override fun onCancelled(error: DatabaseError) {

                callback(false)
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    private fun showProgressBar() {
        binding.progressContainer?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressContainer?.visibility = View.GONE
    }
}
