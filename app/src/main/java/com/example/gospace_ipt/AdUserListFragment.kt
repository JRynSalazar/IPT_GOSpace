package com.example.gospace_ipt


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gospace_ipt.databinding.FragmentAduserListBinding
import com.google.firebase.database.*

class AdUserListFragment : Fragment() {

    private var _binding: FragmentAduserListBinding? = null
    private val binding get() = _binding!!

    private lateinit var userAdapter: UserAdapter
    private var userList: MutableList<User> = mutableListOf()
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAduserListBinding.inflate(inflater, container, false)

        // RecyclerView setup
        binding.UserRecyclerView.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(userList) { user ->
            navigateToUserDetail(user)
        }
        binding.UserRecyclerView.adapter = userAdapter

        // Firebase setup
        database = FirebaseDatabase.getInstance().reference.child("users")
        fetchUserData()

        return binding.root
    }

    private fun fetchUserData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToUserDetail(user: User) {
        val intent = Intent(context, AdminUserInfo::class.java).apply {
            putExtra("name", user.name)
            putExtra("email", user.email)
            putExtra("gender", user.gender)
            putExtra("role", user.role)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

