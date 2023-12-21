package com.capstoneproject.clothizeapp.client.ui.splash

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPrefViewModel
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferences
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferencesFactory
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.dataStore
import com.capstoneproject.clothizeapp.client.ui.client.MainClientActivity
import com.capstoneproject.clothizeapp.client.ui.login.LoginActivity
import com.capstoneproject.clothizeapp.databinding.ActivitySplashBinding
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPrefViewModel
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPreferences
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPreferencesFactory
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.dataTailorStore
import com.capstoneproject.clothizeapp.tailor.ui.MainTailorActivity
import com.capstoneproject.clothizeapp.utils.AnimationPackage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var clientPrefViewModel: ClientPrefViewModel
    private lateinit var tailorPrefViewModel: TailorPrefViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()
        init()
    }

    private fun init() {
        val clientPref = ClientPreferences.getInstance(application.dataStore)
        clientPrefViewModel =
            ViewModelProvider(
                this,
                ClientPreferencesFactory(clientPref)
            )[ClientPrefViewModel::class.java]

        val tailorPref = TailorPreferences.getInstance(application.dataTailorStore)
        tailorPrefViewModel =
            ViewModelProvider(
                this,
                TailorPreferencesFactory(tailorPref)
            )[TailorPrefViewModel::class.java]


        val intentToMainClient = Intent(this, MainClientActivity::class.java)
        val intentToMainTailor = Intent(this, MainTailorActivity::class.java)
        val intentToLogin = Intent(this, LoginActivity::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            clientPrefViewModel.getSessionUser().observe(this@SplashActivity) { session ->
                if (session != null) {
                    startActivity(intentToMainClient)
                    finish()
                } else {
                    tailorPrefViewModel.getSessionUser()
                        .observe(this@SplashActivity) { sessionTailor ->
                            if (sessionTailor != null) {
                                startActivity(intentToMainTailor)
                                finish()
                            } else {
                                startActivity(intentToLogin)
                                finish()
                            }
                        }
                }
            }
        }
    }


    private fun playAnimation() {
        // geser atas cloth sebesar 100dp dan tampilin ing
//        val clothAnim = AnimationPackage.translateY(binding.cloth, 700, 0f, -100f)
//        val ingAnimY = AnimationPackage.translateY(binding.ing, 700, 0f, -100f)
//        val ingAnim = AnimationPackage.fadeIn(binding.ing, 500)
//
//        // show size from left dan hide ize
//        val izeAnim = AnimationPackage.fadeOut(binding.ize, 700)
//        val sAnim = AnimationPackage.fadeIn(binding.s, 500)
//        val sAnimX = AnimationPackage.translateX(binding.s, 700, -30f, 0f)
//
//        // tampilin App
//        val appAnim = AnimationPackage.fadeIn(binding.app, 500)
//        // tampilin box
//        val boxAnim = AnimationPackage.fadeIn(binding.box, 500)
//
//        val ingAnimSet = AnimatorSet().apply {
//            playSequentially(
//                ingAnimY,
//                ingAnim,
//            )
//        }
//
//        val clothingAnim = AnimatorSet().apply {
//            play(clothAnim).with(ingAnimSet)
//        }
//
//        val sizeAnim = AnimatorSet().apply {
//            play(sAnim).with(sAnimX)
//        }


        val logoAnim = AnimationPackage.fadeIn(binding.imgLogo, 1000)
        AnimatorSet().apply {
            playSequentially(
                logoAnim
//                clothingAnim,
//                izeAnim,
//                sizeAnim,
//                appAnim,
//                boxAnim,
            )
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                start()
            }
        }
    }
}