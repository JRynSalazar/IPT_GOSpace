package com.example.gospace_ipt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gospace_ipt.databinding.FragmentAdminRoom1Binding
import com.example.gospace_ipt.databinding.PopupAddRoomBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminRoomFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var binding: FragmentAdminRoom1Binding
    private lateinit var databaseRoom: DatabaseReference
    private lateinit var roomManager: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize binding and database references
        binding = FragmentAdminRoom1Binding.inflate(inflater, container, false)
        databaseRoom = FirebaseDatabase.getInstance().getReference("rooms")
        roomManager = FirebaseDatabase.getInstance().getReference("roomManager")

        // Configure UI
        setupFullScreenMode()
        setupDraggableButton()
        setupViewPagerAndTabs()

        return binding.root
    }

    private fun setupFullScreenMode() {
        activity?.window?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDraggableButton() {
        binding.draggableButton.setOnClickListener { showPopup(it) }
        makeButtonDraggable(binding.draggableButton)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun makeButtonDraggable(button: View) {
        button.setOnTouchListener(object : View.OnTouchListener {
            var dX = 0f
            var dY = 0f
            var lastAction = 0

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        lastAction = MotionEvent.ACTION_DOWN
                    }
                    MotionEvent.ACTION_MOVE -> {
                        view.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
                        lastAction = MotionEvent.ACTION_MOVE
                    }
                    MotionEvent.ACTION_UP -> {
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            view.performClick()
                        }
                    }
                    else -> return false
                }
                return true
            }
        })
    }

    private fun setupViewPagerAndTabs() {
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        viewPager.adapter = RoomFragmentAdapter(requireActivity())

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pending Room"
                1 -> "All Room"
                2 -> "Available"
                3 -> "Unavailable"
                else -> null
            }
        }.attach()
    }

    private fun showPopup(anchorView: View) {
        val popupBinding = PopupAddRoomBinding.inflate(LayoutInflater.from(requireContext()))
        val popupWindow = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setBackgroundDrawable(requireContext().getDrawable(R.drawable.popup_background))

        // Cancel button
        popupBinding.cancelBTN.setOnClickListener { popupWindow.dismiss() }

        // Add Room button
        popupBinding.addRoomBTN.setOnClickListener {
            val roomName = popupBinding.username.text.toString()
            if (roomName.isNotEmpty()) {
                saveRoomData(roomName)
                saveRoomManagerData(roomName)
                popupWindow.dismiss()
            } else {
                Toast.makeText(requireContext(), "Room name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        popupWindow.isOutsideTouchable = true
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }

    private fun saveRoomData(roomName: String) {
        databaseRoom.child(roomName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Room already exists
                    Toast.makeText(requireContext(), "Room already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // Room does not exist, create a new one
                    val room = RoomList(roomName = roomName)
                    databaseRoom.child(roomName).setValue(room)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Room added successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveRoomManagerData(roomName: String, status: String = "Available", type: String = "Normal") {
        roomManager.child(roomName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Room manager data already exists
                    Toast.makeText(requireContext(), "Room Manager data already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // Room manager data does not exist, create a new one
                    val roomManagerData = RoomMng(roomName = roomName, status = status, type = type)
                    roomManager.child(roomName).setValue(roomManagerData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Room Manager added successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
