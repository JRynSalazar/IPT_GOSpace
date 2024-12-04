package com.example.gospace_ipt

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding

open class BaseActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupProgressBar()
    }

    private fun setupProgressBar() {

        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
            setPadding(40)
        }

        // Get the root layout of the current activity view
        val layout = findViewById<ConstraintLayout>(android.R.id.content)
        layout?.addView(progressBar)

        // Set constraints to center the ProgressBar in the screen
        val set = ConstraintSet()
        set.clone(layout)
        set.connect(progressBar.id, ConstraintSet.TOP, layout.id, ConstraintSet.TOP)
        set.connect(progressBar.id, ConstraintSet.BOTTOM, layout.id, ConstraintSet.BOTTOM)
        set.connect(progressBar.id, ConstraintSet.START, layout.id, ConstraintSet.START)
        set.connect(progressBar.id, ConstraintSet.END, layout.id, ConstraintSet.END)
        set.applyTo(layout)
    }

    // Method to show the loading spinner
    fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    // Method to hide the loading spinner
    fun hideLoading() {
        progressBar.visibility = View.GONE
    }
}
