package com.example.gospace_ipt


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gospace_ipt.databinding.UserItemListsBinding

class AdapterUserList(private val userList: List<User>) : RecyclerView.Adapter<AdapterUserList.UserViewHolder>() {

    inner class UserViewHolder(val binding: UserItemListsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        with(holder.binding) {
            username.text = user.name
            role.text = user.role
            Glide.with(profileImg.context).load(user.img).into(profileImg)

            delete.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int = userList.size
}