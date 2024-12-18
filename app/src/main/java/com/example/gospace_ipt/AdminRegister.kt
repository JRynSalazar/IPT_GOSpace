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
import com.example.gospace_ipt.databinding.ActivityAdminRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt

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
            val back = Intent(this, AdminSignIn::class.java)
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
                    if (role == "Admin Root" || role == "GSO") {
                        checkUserRole(role, name) { exists ->
                            if (!exists) {
                                hideProgressBar()
                                Toast.makeText(
                                    this@AdminRegister,
                                    "An account with the same role or name already exists.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Proceed to create the account since role doesn't exist
                                createAccountInFirebaseAuth(email, password, name, gender, role)
                            }
                        }
                    } else {
                        // Non-restricted roles can proceed directly
                        createAccountInFirebaseAuth(email, password, name, gender, role)
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

    private fun saveUserToDatabase(userId: String, email: String, plainPassword: String, name: String, gender: String, role: String) {
        val hashedPassword = hashPassword(plainPassword) // Hash the password before saving
        val user = User(email, hashedPassword, name, gender, role)

        database.child(userId).setValue(user)
            .addOnCompleteListener { dbTask ->
                hideProgressBar()
                if (dbTask.isSuccessful) {
                    Toast.makeText(this@AdminRegister, "Account registered successfully.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AdminRegister, AdminSignIn::class.java))
                    finish()
                } else {
                    Toast.makeText(this@AdminRegister, "Failed to save user data. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                hideProgressBar()
                Toast.makeText(this@AdminRegister, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    private fun createAccountInFirebaseAuth(email: String, password: String, name: String, gender: String, role: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUserId = firebaseAuth.currentUser?.uid
                    if (firebaseUserId != null) {
                        saveUserToDatabase(firebaseUserId, email, password, name, gender, role)
                        saveUserToFirestore(firebaseUserId, email, name, role)
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

    private fun checkUserRole(role: String, name: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("role", role)
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                callback(querySnapshot.isEmpty)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@AdminRegister, "Error checking role: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }
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

    private fun saveUserToFirestore(userId: String, email: String, name: String, role: String) {
        val user = hashMapOf(
            "email" to email,
            "name" to name,
            "role" to role
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                hideProgressBar()
                Toast.makeText(this@AdminRegister, "Account data saved to Firestore successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                hideProgressBar()
                Toast.makeText(this@AdminRegister, "Error saving user data to Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
