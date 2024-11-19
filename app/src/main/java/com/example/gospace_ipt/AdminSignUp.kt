package com.example.gospace_ipt

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gospace_ipt.databinding.ActivityAdminSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminSignUp : AppCompatActivity() {

    private lateinit var binding: ActivityAdminSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")



        // -----Loading-----------
      /*  val progressBar = findViewById<ImageView>(R.id.animatedProgressBar)
        Glide.with(this)
            .asGif()
            .load(R.drawable.ic_loading)
            .into(progressBar)
*/


        binding.back.setOnClickListener {
            val back = Intent(this, ChooseUser::class.java)
            startActivity(back)
        }

        binding.register.setOnClickListener {
            val toRegister = Intent(this, AdminRegister::class.java)
            startActivity(toRegister)
        }

        binding.adminLogin.setOnClickListener {
            val email = binding.username.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loadData(true)

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        loadData(false)
                        if (userId != null) {
                            database.child(userId).get().addOnCompleteListener { roleTask ->
                                if (roleTask.isSuccessful) {
                                    val role = roleTask.result.child("role").getValue(String::class.java)
                                    navigateToRoleBasedUI(role)
                                } else {
                                    Toast.makeText(this, "Failed to retrieve user role.", Toast.LENGTH_SHORT).show()
                                    loadData(false)
                                }
                            }
                        } else {
                            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show()
                            loadData(false)
                        }
                    } else {
                        Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                        loadData(false)
                    }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
                loadData(false)
            }
        }
    }

    private fun navigateToRoleBasedUI(role: String?) {
        when (role) {
            "Admin Root" -> {
                val toAdminRoot = Intent(this, AdminViewpage::class.java)
                startActivity(toAdminRoot)
            }
            "GSO" -> {
                val toGSO = Intent(this, GSOViewPage::class.java)
                startActivity(toGSO)
            }
            else -> {
                Toast.makeText(this, "Role not recognized.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
    private fun showProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.GONE
    }
*/
    private fun loadData(isLoading: Boolean) {
        val progressBar: ProgressBar = findViewById(R.id.rotatingProgressBar)
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
        if (isLoading) {
            Handler().postDelayed({
                loadData(false)
                Toast.makeText(this, "Data Loaded Successfully", Toast.LENGTH_SHORT).show()
            }, 3000)
        }
    }
}
