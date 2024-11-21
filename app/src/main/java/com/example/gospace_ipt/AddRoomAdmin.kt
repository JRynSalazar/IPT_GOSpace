package com.example.gospace_ipt

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gospace_ipt.databinding.ActivityAddroomAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddRoomAdmin : AppCompatActivity() {
    private lateinit var database1: DatabaseReference
    private lateinit var binding: ActivityAddroomAdminBinding
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddroomAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database1 = database.getReference("users") // Initialize database reference for users

        // Back button functionality
        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val roomName = intent.getStringExtra("room_name")
        val status = intent.getStringExtra("status")

        // Display room data
        binding.roomName.text = roomName
        binding.status.text = status

        // Set the click listeners for the buttons
        binding.scheduleButton.setOnClickListener {
            showDateTimePickerDialog(roomName)
        }

        binding.requestBTN.setOnClickListener {

            handleRoomRequest(roomName)
        }
    }

    private fun showDateTimePickerDialog(roomName: String?) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val dateFormat = SimpleDateFormat("EEEE, yyyy-MM-dd", Locale.getDefault())
                val selectedDate = dateFormat.format(selectedCalendar.time)

                val startTimePicker = TimePickerDialog(
                    this,
                    { _, startHour, startMinute ->
                        val startAmPm = if (startHour < 12) "AM" else "PM"
                        val formattedStartHour = if (startHour % 12 == 0) 12 else startHour % 12
                        val startFormattedTime =
                            String.format(Locale.getDefault(), "%02d:%02d %s", formattedStartHour, startMinute, startAmPm)
                        val startCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth, startHour, startMinute)
                        }

                        val currentCalendar = Calendar.getInstance()

                        if (startCalendar.before(currentCalendar)) {
                            Toast.makeText(this, "Start time cannot be in the past", Toast.LENGTH_SHORT).show()
                        } else {
                            val endTimePicker = TimePickerDialog(
                                this,
                                { _, endHour, endMinute ->
                                    val endAmPm = if (endHour < 12) "AM" else "PM"
                                    val formattedEndHour = if (endHour % 12 == 0) 12 else endHour % 12
                                    val endFormattedTime =
                                        String.format(Locale.getDefault(), "%02d:%02d %s", formattedEndHour, endMinute, endAmPm)

                                    val endCalendar = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth, endHour, endMinute)
                                    }

                                    if (endCalendar.before(startCalendar)) {
                                        Toast.makeText(this, "End time cannot be earlier than start time", Toast.LENGTH_SHORT).show()
                                    } else {
                                        binding.scheduleText.text =
                                            "Scheduled: $selectedDate\nFrom $startFormattedTime to $endFormattedTime"
                                    }
                                },
                                startHour,
                                startMinute,
                                false
                            )
                            endTimePicker.setTitle("Select End Time")
                            endTimePicker.show()
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                startTimePicker.setTitle("Select Start Time")
                startTimePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.setTitle("Select Date")
        datePicker.show()
    }

    private fun handleRoomRequest(roomName: String?) {
        val currentUser = firebaseAuth.currentUser?.uid
        if (currentUser != null) {
            database.getReference("users").child(currentUser).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").value?.toString() ?: "Unknown Name"
                    val role = dataSnapshot.child("role").value?.toString() ?: "Unknown Role"

                    // Update UI
                    binding.name.text = name
                    binding.role.text = role

                    // Prepare updated data
                    val updates = mapOf(
                        "status" to "Unavailable",
                        "borrower" to name,
                        "role" to role,
                        "schedule" to binding.scheduleText.text.toString(),
                        "message" to "Room requested"
                    )

                    // Update the specific room node in Firebase
                    val roomRef = database.getReference("rooms").child(roomName ?: "Unknown Room")
                    roomRef.updateChildren(updates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Room request updated successfully", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK) // Notify AllRoomFragment of changes
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to update room request", Toast.LENGTH_SHORT).show()
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

}
