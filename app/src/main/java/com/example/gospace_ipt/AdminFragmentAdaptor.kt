package com.example.ipt2_login

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gospace_ipt.AdProfileFragment
import com.example.gospace_ipt.AdRoomFragment
import com.example.gospace_ipt.AdminHome_Fragment

class AdminFragmentAdaptor(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdminHome_Fragment()
            1 -> AdRoomFragment()
            2 -> AdProfileFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
