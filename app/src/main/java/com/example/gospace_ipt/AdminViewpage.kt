package com.example.gospace_ipt

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdminViewpage : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_viewpage)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        viewPager = findViewById(R.id.viewpage)

        viewPager.adapter = AdminFragmentAdaptor(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    //  tab.text = "Home"
                    tab.setIcon(R.drawable.homeicon)
                }
                1 -> {
                    //  tab.text = "Room"
                    tab.setIcon(R.drawable.room1)
                }
                2 -> {
                    // tab.text = "Profile"
                    tab.setIcon(R.drawable.profileicon)
                }
            }
        }.attach()


    }
}