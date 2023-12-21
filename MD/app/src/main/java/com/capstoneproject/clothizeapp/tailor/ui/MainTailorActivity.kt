package com.capstoneproject.clothizeapp.tailor.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.databinding.ActivityMainTailorBinding

class MainTailorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainTailorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTailorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }

    private fun init(){
        val navController = findNavController(R.id.nav_host_fragment_tailor)
        binding.bottomNav.setupWithNavController(navController)
    }
}