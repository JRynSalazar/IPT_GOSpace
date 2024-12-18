package com.example.gospace_ipt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.gospace_ipt.databinding.FragmentAdProfileBinding
import com.example.gospace_ipt.databinding.PopupAddRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdProfileFragment : Fragment() {



    private lateinit var binding: FragmentAdProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var databaseRoom: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        databaseRoom = FirebaseDatabase.getInstance().getReference("rooms")

        binding.addRoom1.setOnClickListener {
            showPopup(it)
        }

     //---------------Loading-----------
        val progressBar = binding.animatedProgressBar
        Glide.with(this)
            .asGif()
            .load(R.drawable.ic_loading)
            .into(progressBar)



        fetchUserData()

        binding.addUser.setOnClickListener{
            val toAddUser = Intent(requireActivity(), AddUserAccnt::class.java)
            startActivity(toAddUser)
        }

        binding.logOut.setOnClickListener {
            showProgressBar()
            logoutNotif()
        }

        binding.accSettings.setOnClickListener {
            val toAccntSetting = Intent(requireActivity(), AdminAccntSettings::class.java)
            startActivity(toAccntSetting)
        }

        binding.upload.setOnClickListener {

        }
    }



    private fun fetchUserData() {
        showProgressBar()

        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId != null) {
            database.child(currentUserId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").value.toString()
                    val role = dataSnapshot.child("role").value.toString()
                    binding.name.text = name
                    binding.role.text = role
                } else {
                    binding.name.text = "Name not found"
                    binding.role.text = "Role not found"
                }
                hideProgressBar()
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
        } else {
            binding.name.text = "User not logged in"
            binding.role.text = ""
            hideProgressBar()
        }

    }

    private fun logoutNotif() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { dialog, which ->
            logoutUser()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    //----------------logout function---------------
    private fun logoutUser() {
        try {
            firebaseAuth.signOut()
            Toast.makeText(requireContext(), "You have been logged out.", Toast.LENGTH_SHORT).show()
            toChooseUser()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error during logout: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toChooseUser() {
        val intent = Intent(requireContext(), ChooseUser::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

        // Ensure fragment's activity finishes to avoid leaks
        activity?.finish()
    }

    private fun showProgressBar() {
        binding.progressContainer.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressContainer.visibility = View.GONE
    }
    override fun onResume() {
        super.onResume()
        fetchUserData()
    }
    private fun showPopup(anchorView: View) {
        val popupBinding = PopupAddRoomBinding.inflate(LayoutInflater.from(requireContext()))
        val popupView = popupBinding.root
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setBackgroundDrawable(requireContext().getDrawable(R.drawable.popup_background))

        popupBinding.cancelBTN.setOnClickListener {
            popupWindow.dismiss()
        }

        popupBinding.addRoomBTN.setOnClickListener {
            val roomName = popupBinding.username.text.toString()
            if (roomName.isNotEmpty()) {
                val room = RoomList(roomName = roomName)
                val roomId = roomName
                if (roomId != null) {
                    databaseRoom.child(roomId).setValue(room)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Room added successfully", Toast.LENGTH_SHORT).show()
                            popupWindow.dismiss()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Room name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        popupWindow.isOutsideTouchable = true
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }

}

