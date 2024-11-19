package com.example.gospace_ipt

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.gospace_ipt.databinding.ActivityChooseUser2Binding
import com.google.firebase.database.FirebaseDatabase


class ChooseUser : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityChooseUser2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)


        val testDatabase = FirebaseDatabase.getInstance().getReference("test")
        testDatabase.setValue("Hello Firebase!")
            .addOnSuccessListener {
                Log.d("Firebase", "Data written successfully!")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error writing data", it)
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
            val toReg = Intent(this, AdminSignUp::class.java)
            startActivity(toReg)
            hideProgressBar()
        }
        binding.toUserLogin.setOnClickListener {
            showProgressBar()
            val toReg = Intent(this, UserLogin::class.java)
            startActivity(toReg)
            hideProgressBar()
        }
//------------------------------------------------------------------------
    }
    private fun showProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        findViewById<View>(R.id.progressContainer)?.visibility = View.GONE
    }
}