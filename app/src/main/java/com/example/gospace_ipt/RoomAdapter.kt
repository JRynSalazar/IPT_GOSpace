package com.example.gospace_ipt


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RoomAdapter(
    private val roomList: MutableList<RoomList>,
    private val onRoomClick: (RoomList) -> Unit,
    private val onRoomDelete: (RoomList) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomNumber: TextView = view.findViewById(R.id.roomNumber)
        val status: TextView = view.findViewById(R.id.status)
        val profileImg: ImageView = view.findViewById(R.id.profileImg)
        val statusDot: ImageView = view.findViewById(R.id.imageView6)
        val trashButton: ImageButton = view.findViewById(R.id.trashButton)

        fun bind(room: RoomList) {
            roomNumber.text = room.roomName
            status.text = room.status

            Glide.with(itemView.context)
                .load(R.drawable.aces_logo)
                .into(profileImg)

            val statusDrawable = if (room.status == "Available") {
                R.drawable.green_dot
            } else {
                R.drawable.red_dot
            }
            statusDot.setImageResource(statusDrawable)

            itemView.setOnClickListener {
                onRoomClick(room)
            }

            trashButton.setOnClickListener {
                onRoomDelete(room)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_list, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.bind(room)
    }

    override fun getItemCount(): Int = roomList.size

    fun removeItem(room: RoomList) {
        val position = roomList.indexOf(room)
        if (position != -1) {
            roomList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}


