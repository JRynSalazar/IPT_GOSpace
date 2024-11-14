package com.example.gospace_ipt

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gospace_ipt.databinding.ActivityChooseUser2Binding




class ChooseUser : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityChooseUser2Binding
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()




        binding = ActivityChooseUser2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)

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
            showLoading()
            val toReg = Intent(this, AdminSignUp::class.java)
            startActivity(toReg)
            hideLoading()
        }
        binding.toUserLogin.setOnClickListener {
            showLoading()
            val toReg = Intent(this, UserLogin::class.java)
            startActivity(toReg)
            hideLoading()
        }
//------------------------------------------------------------------------
    }
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
        }, 500)
    }
}