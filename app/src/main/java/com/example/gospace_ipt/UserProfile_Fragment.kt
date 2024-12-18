package com.example.gospace_ipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.gospace_ipt.databinding.FragmentUserProfileBinding


class UserProfile_Fragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

}