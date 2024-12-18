package com.example.gospace_ipt

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.gospace_ipt.databinding.ActivityChooseUser2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class ChooseUser : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityChooseUser2Binding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )


        auth = FirebaseAuth.getInstance()


        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserRole(currentUser.uid)
        }


        binding = ActivityChooseUser2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // -----Loading-----------
        val progressBar = findViewById<ImageView>(R.id.animatedProgressBar)
        Glide.with(this)
            .asGif()
            .load(R.drawable.ic_loading)
            .into(progressBar)

//--------------------------IMG GIF----------------------------------
        val imageView: ImageView = binding.mygif

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val gif = ContextCompat.getDrawable(this, R.drawable.gif_logo) as? AnimatedImageDrawable
            gif?.apply {
                repeatCount = 1
                imageView.setImageDrawable(this)
                start()
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic("notifications")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to notifications"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d("FCM", msg)
            }

//-------------------------------------------------------------------

//-----------------------ONBOARDING appearing only once-------------------

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", false)


        if (isFirstRun) {
            startActivity(Intent(this, ActivityOnboarding::class.java))
            sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
            finish()
            return
        }
//-----------------------------------------------------------------------

//------------------Navigation section----------------------------------

        binding.toAdminLogin.setOnClickListener {
            showProgressBar()
            val toReg = Intent(this, AdminSignIn::class.java)
            startActivity(toReg)
            hideProgressBar()
        }
        binding.toUserLogin.setOnClickListener {
            showProgressBar()
            val toUser = Intent(this, UserLogin::class.java)
            startActivity(toUser)
            hideProgressBar()
        }
//------------------------------------------------------------------------
    }

    private fun fetchUserRole(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role")
                    navigateToRoleBasedUI(role)
                } else {
                }
            }
            .addOnFailureListener { e ->
            }
    }

    private fun navigateToRoleBasedUI(role: String?) {
        when (role) {
            "Admin Root" -> startActivity(Intent(this, AdminViewpage::class.java))
            "GSO" -> startActivity(Intent(this, GSOViewPage::class.java))
            "Faculty", "Staff", "Student" -> startActivity(Intent(this, UserViewerPage::class.java))
            else -> {
            //    startActivity(Intent(this, UserViewerPage::class.java))
               auth.signOut()
            }
        }
        finish()
    }


    private fun showProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.GONE
    }
}