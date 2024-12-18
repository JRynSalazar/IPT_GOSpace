package com.example.gospace_ipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.gospace_ipt.databinding.FragmentGSOProfileBinding

class GSOProfile_Fragment : Fragment() {
    private lateinit var binding: FragmentGSOProfileBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGSOProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


}