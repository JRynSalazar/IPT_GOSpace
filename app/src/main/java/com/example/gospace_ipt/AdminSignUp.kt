package com.example.gospace_ipt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gospace_ipt.databinding.ActivityAdminSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class AdminSignUp : AppCompatActivity() {


    private lateinit var binding: ActivityAdminSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar


    val user: String = "admin"
    var pass: String = "admin@123"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        binding = ActivityAdminSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.adminLogin.setOnClickListener{

            val username = binding.username.text.toString()
            val password = binding.etPassword.text.toString()


            if (username.isNotEmpty() || password.isNotEmpty()) {
                showLoading()

                firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener {
                    hideLoading()
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, GSOViewPage::class.java)
                        startActivity(intent)
                    } else if (username == user && password == pass) {
                        val toAdminRoot = Intent(this, AdminViewpage::class.java)
                        startActivity(toAdminRoot)
                    } else {
                        Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }


    fun hideLoading() {
        progressBar.visibility = View.GONE
    }

}