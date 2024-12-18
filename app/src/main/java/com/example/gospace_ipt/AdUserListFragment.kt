package com.example.gospace_ipt


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
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

        binding.draggableButton.setOnClickListener{
            val toAddUser = Intent(requireActivity(), AddUserAccnt::class.java)
            startActivity(toAddUser)
        }


        makeButtonDraggable(binding.draggableButton)

        binding.UserRecyclerView.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(userList) { user ->
            navigateToUserDetail(user)
        }
        binding.UserRecyclerView.adapter = userAdapter

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
                            // The button was tapped, trigger click action
                            view.performClick()
                        }
                    }
                    else -> return false
                }
                return true
            }
        })
    }

}

