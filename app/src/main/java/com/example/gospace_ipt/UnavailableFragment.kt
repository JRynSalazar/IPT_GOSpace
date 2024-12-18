package com.example.gospace_ipt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UnavailableFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var roomAdapter: RoomAdapter
    private var roomList: MutableList<RoomList> = mutableListOf()
    private lateinit var database: DatabaseReference
    private lateinit var userDatabase: DatabaseReference
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_all_room, container, false)

        recyclerView = rootView.findViewById(R.id.roomRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        database = FirebaseDatabase.getInstance().reference.child("rooms")
        userDatabase = FirebaseDatabase.getInstance().reference.child("users")

        roomAdapter = RoomAdapter(roomList, { room ->
            navigateToRoomDetail(room)
        }, { room ->
            deleteRoom(room)
        })
        recyclerView.adapter = roomAdapter

        fetchRoomData()

        return rootView
    }

    private fun fetchRoomData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomList.clear()
                for (roomSnapshot in snapshot.children) {
                    val room = roomSnapshot.getValue(RoomList::class.java)
                    if (room != null && room.status == "Unavailable") {
                        roomList.add(room)
                    }
                }
                roomAdapter.notifyDataSetChanged()

                if (roomList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToRoomDetail(room: RoomList) {
        val currentUserId = firebaseAuth.currentUser?.uid

        if (currentUserId != null) {
            userDatabase.child(currentUserId).child("role").get().addOnSuccessListener { dataSnapshot ->
                val role = dataSnapshot.value?.toString()

                if (role == "Admin Root" || role == "GSO") {
                    val intent = Intent(context, AddRoomAdmin::class.java).apply {
                        putExtra("room_name", room.roomName)
                        putExtra("status", room.status)
                        putExtra("borrower", room.borrower)
                        putExtra("role", room.role)
                        putExtra("schedule", room.schedule)
                        putExtra("message", room.message)
                    }
                    startActivity(intent)
                } else {
                    val intent = Intent(context, UserRequestRoom::class.java).apply {
                        putExtra("room_name", room.roomName)
                        putExtra("status", room.status)
                        putExtra("borrower", room.borrower)
                        putExtra("role", room.role)
                        putExtra("schedule", room.schedule)
                        putExtra("message", room.message)
                    }
                    startActivity(intent)
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
    private fun deleteRoom(room: RoomList) {
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId != null) {

            userDatabase.child(currentUserId).child("role").get().addOnSuccessListener { dataSnapshot ->
                val role = dataSnapshot.value?.toString()

                if (role == "Admin Root" || role == "GSO") {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle("Delete Room")
                            .setMessage("Are you sure you want to delete ${room.roomName}?")
                            .setPositiveButton("Yes") { _, _ ->
                                database.child(room.roomName).removeValue()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Room deleted successfully", Toast.LENGTH_SHORT).show()
                                        roomAdapter.removeItem(room)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to delete room", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                } else {
                    Toast.makeText(context, "You do not have permission to delete this room.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to fetch user role.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

}