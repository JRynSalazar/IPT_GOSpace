package com.example.gospace_ipt

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gospace_ipt.databinding.ActivityAddroomAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddRoomAdmin : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var database1: DatabaseReference
    private lateinit var databaseR: DatabaseReference
    private lateinit var binding: ActivityAddroomAdminBinding
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddroomAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        database1 = database.getReference("users")
        databaseR = database.getReference("rooms")

        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val roomName = intent.getStringExtra("room_name")
        val status = intent.getStringExtra("status")
        val borrower = intent.getStringExtra("borrower")
        val role = intent.getStringExtra("role")
        val schedule = intent.getStringExtra("schedule")
        val message = intent.getStringExtra("message")
        val state = "Available"

        binding.roomName.text = roomName
        binding.status.text = status
        binding.name.text = borrower
        binding.role.text = role
        binding.scheduleText.text = schedule
        binding.message.text = Editable.Factory.getInstance().newEditable(message)



        val statusDrawable = if (status == "Available" || status == "Pending") {
            R.drawable.green_dot
        } else {
            R.drawable.red_dot
        }
        binding.availability.setImageResource(statusDrawable)


        if (status == "Available") {
            binding.accptBTN.visibility = View.GONE
            binding.declineBTN.visibility = View.GONE

        }else if(status == "Unavailable"){
            binding.accptBTN.visibility = View.GONE
            binding.declineBTN.visibility = View.VISIBLE
        }else {
            binding.accptBTN.visibility = View.VISIBLE
            binding.declineBTN.visibility = View.VISIBLE
        }

        binding.accptBTN.setOnClickListener {
            handleRoomRequest(roomName)
        }

        binding.declineBTN.setOnClickListener {
            handleRoomDecline(roomName)
        }
    }

    private fun handleRoomRequest(roomName: String?) {
        val currentUser = firebaseAuth.currentUser?.uid
        if (currentUser != null) {
            database.getReference("users").child(currentUser).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        val name = dataSnapshot.child("name").value?.toString() ?: "Unknown Name"
                        val role = dataSnapshot.child("role").value?.toString() ?: "Unknown Role"

                        val schedule = binding.scheduleText.text.toString()
                        val updates = mapOf(
                            "status" to "Unavailable",
                       //     "borrower" to name,
                       //     "role" to role,
                            "schedule" to schedule,
                            "message" to "Room requested",
                            "state" to "Unavailable"
                        )

                        // Update the room database
                        val roomRef =
                            database.getReference("rooms").child(roomName ?: "Unknown Room")
                        roomRef.updateChildren(updates).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val notificationMessage =
                                    "--$roomName has been Accepted by $currentUser at $schedule"
                                if (roomName != null) {
                                    addNotificationToDatabase(notificationMessage, roomName)
                                    Toast.makeText(
                                        this,
                                        "Room request updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    setResult(RESULT_OK)
                                    finish()
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to update room request",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRoomDecline(roomName: String?) {
        val updates = mapOf(
            "borrower" to "None",
            "role" to "None",
            "schedule" to "None",
            "status" to "Available",
            "message" to "Room request canceled",
            "state" to "Available"
        )

        // Update the room database to reset values
        val roomRef = database.getReference("rooms").child(roomName ?: "Unknown Room")
        roomRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Room request has been canceled", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Failed to cancel room request", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addNotificationToDatabase(notificationMessage: String, roomName: String) {
        val notificationRef = database.getReference("notifications")
        val notificationId = notificationRef.push().key

        if (notificationId != null) {
            val notificationData = mapOf(
                "id" to notificationId,
                "message" to notificationMessage,
                "roomName" to roomName,
                "timestamp" to System.currentTimeMillis()
            )

            notificationRef.child(notificationId).setValue(notificationData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Notification added", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to add notification", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }
}
