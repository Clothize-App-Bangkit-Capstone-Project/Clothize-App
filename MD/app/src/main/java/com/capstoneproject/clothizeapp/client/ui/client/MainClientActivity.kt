package com.capstoneproject.clothizeapp.client.ui.client

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.ui.client.measurements.MeasurementActivity
import com.capstoneproject.clothizeapp.databinding.ActivityMainClientBinding

class MainClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainClientBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()


    }


    private fun init(){
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setupWithNavController(navController)

        binding.fabMeasurement.setOnClickListener {
            val intentToMeasurement = Intent(this, MeasurementActivity::class.java)
            startActivity(intentToMeasurement)
        }
    }
}