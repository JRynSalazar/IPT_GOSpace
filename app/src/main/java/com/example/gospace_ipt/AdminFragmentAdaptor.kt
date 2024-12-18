package com.example.gospace_ipt

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdminFragmentAdaptor(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentAdminCensus()
            1 -> AdminRoomFragment()
            2 -> AdUserListFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
