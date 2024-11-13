package com.example.gospace_ipt

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gospace_ipt.databinding.ActivityChooseUserBinding



class ChooseUser : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityChooseUserBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_user)

        binding = ActivityChooseUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            startActivity(Intent(this, Onboarding::class.java))
            sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
            finish()
            return
        }
//-----------------------------------------------------------------------

//------------------Navigation section----------------------------------

        binding.toAdminLogin.setOnClickListener {
            val toReg = Intent(this, AdminSignUp::class.java)
            startActivity(toReg)
        }
        binding.toUserLogin.setOnClickListener {
            val toReg = Intent(this, UserLogin::class.java)
            startActivity(toReg)
        }
//------------------------------------------------------------------------
    }
}