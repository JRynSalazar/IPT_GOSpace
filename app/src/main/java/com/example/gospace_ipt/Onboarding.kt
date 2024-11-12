package com.example.gospace_ipt

import android.content.Intent
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gospace_ipt.databinding.ActivityMainBinding

class Onboarding : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//----------------------------IMG GIF ------------------------------
        val imageView: ImageView = binding.mygif

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val gif = ContextCompat.getDrawable(this, R.drawable.gif_logo) as? AnimatedImageDrawable
            gif?.apply {
                repeatCount = 1
                imageView.setImageDrawable(this)
                start()
            }
        }
//-----------------------------------------------------------------


        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
