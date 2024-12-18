package com.example.gospace_ipt

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RoomFragmentAdapter (activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            0 -> FragmentRoomPending()
            1 -> AllRoomFragment()
            2 -> AvailableFragment()
            3 -> UnavailableFragment()

            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
