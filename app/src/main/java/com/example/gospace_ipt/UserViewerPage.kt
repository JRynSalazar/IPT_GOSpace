package com.example.gospace_ipt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.gospace_ipt.databinding.ActivityAdminViewpageBinding
import com.example.gospace_ipt.databinding.ActivityUserViewerPageBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserViewerPage : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityUserViewerPageBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserViewerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        notificationAdapter = NotificationAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = notificationAdapter


        viewPager = binding.viewpage
        tabLayout = binding.tabLayout

        firebaseAuth = FirebaseAuth.getInstance()

        viewPager.adapter = UserFragmentAdapter(this)

        fetchNotifications()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    //  tab.text = "Home"
                    tab.setIcon(R.drawable.homeicon)
                }
                1 -> {
                    //  tab.text = "Room"
                    tab.setIcon(R.drawable.profileicon)
                }

            }
        }.attach()


        binding.notif.setOnClickListener{
            if(binding.recycler.visibility == View.GONE){
                binding.recycler.visibility = View.VISIBLE
                binding.newNotif.visibility = View.GONE
            }else{
                binding.recycler.visibility = View.GONE
                //    binding.newNotif.visibility = View.VISIBLE
            }

        }

        binding.toolbar.setOnClickListener {
            showPopupMenu()
        }
    }

    private fun fetchNotifications() {
        val notificationRef = database.child("notifications")
        notificationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = mutableListOf<Notification>()
                for (data in snapshot.children) {
                    val id = data.key ?: ""
                    val message = data.child("message").value.toString()
                    val timestamp = data.child("timestamp").value?.toString() // Optional
                    notifications.add(Notification(id, message, timestamp))
                }

                // Update the RecyclerView
                notificationAdapter.updateNotifications(notifications)

                // Show the `newNotif` view when there are notifications
                if (notifications.isNotEmpty()) {
                    binding.newNotif.visibility = View.VISIBLE
                } else {
                    binding.newNotif.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors if needed
            }
        })
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.toolbar)
        popupMenu.menuInflater.inflate(R.menu.setting_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.account_settings -> {
                    startActivity(Intent(this, AdminAccntSettings::class.java))
                    true
                }
                R.id.terms -> {
                    startActivity(Intent(this, TermsCondition::class.java))
                    true
                }
                R.id.logout -> {
                    logoutNotif()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    // Show confirmation dialog for logout
    private fun logoutNotif() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            logoutUser()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }


    private fun logoutUser() {
        try {
            firebaseAuth.signOut()
            toChooseUser()
        } catch (e: Exception) {

        }
    }


    private fun toChooseUser() {
        val intent = Intent(this, ChooseUser::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}