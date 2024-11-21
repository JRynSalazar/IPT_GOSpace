package com.example.gospace_ipt

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AllRoomFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var roomAdapter: RoomAdapter
    private var roomList: MutableList<RoomList> = mutableListOf()
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_all_room, container, false)

        recyclerView = rootView.findViewById(R.id.roomRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        database = FirebaseDatabase.getInstance().reference.child("rooms")

        roomAdapter = RoomAdapter(roomList) { room ->
            navigateToRoomDetail(room)
        }
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
                    if (room != null) {
                        roomList.add(room)
                    }
                }
                roomAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToRoomDetail(room: RoomList) {
        val intent = Intent(context, AddRoomAdmin::class.java).apply {
            putExtra("room_name", room.roomName)
            putExtra("status", room.status)
            putExtra("borrower", room.borrower)
            putExtra("role", room.role)
            putExtra("schedule", room.schedule)
            putExtra("message", room.message)
        }
        startActivity(intent)
    }
}