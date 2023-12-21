package com.capstoneproject.clothizeapp.client.ui.register

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.ui.login.LoginActivity
import com.capstoneproject.clothizeapp.databinding.ActivityRegisterBinding
import com.capstoneproject.clothizeapp.utils.AnimationPackage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: AlertDialog
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()
        init()


    }

    private fun init() {
        auth = Firebase.auth
        db = Firebase.firestore
        loadingDialog = loadingDialog()

        binding.btnRegisBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnRegis.setOnClickListener {
            if (checkFillOrNot()) {
                setNewUser()
            }
        }
    }

    private fun checkFillOrNot(): Boolean {
        if (binding.nameRegis.text.toString().isEmpty()) {
            binding.nameEditTextLayout.error = getString(R.string.not_fill)
        } else {
            binding.nameEditTextLayout.error = null
            binding.nameEditTextLayout.isErrorEnabled = false
            binding.nameEditTextLayout.clearFocus()
            binding.emailEditTextLayout.requestFocus()
        }

        if (binding.emailRegis.text.toString().isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.not_fill)
            binding.emailRegis.error = null
        } else if (binding.emailRegis.error != null) {
            binding.emailEditTextLayout.isErrorEnabled = false
            binding.emailEditTextLayout.error = null
        } else {
            binding.emailEditTextLayout.isErrorEnabled = false
            binding.emailEditTextLayout.clearFocus()
            binding.passwordEditTextLayout.requestFocus()
        }

        if (binding.passwordRegis.text.toString().isEmpty()) {
            binding.passwordEditTextLayout.error = getString(R.string.not_fill)
            binding.passwordRegis.error = null
        } else if (binding.passwordRegis.error != null) {
            binding.passwordEditTextLayout.isErrorEnabled = false
            binding.passwordEditTextLayout.error = null
        } else {

            binding.passwordEditTextLayout.isErrorEnabled = false
            binding.passwordEditTextLayout.clearFocus()
        }

        if (binding.passwordRegisConfirm.text.toString().isEmpty()) {
            binding.passwordConfirmEditTextLayout.error = getString(R.string.not_fill)
        } else if (binding.passwordRegisConfirm.text.toString() != binding.passwordRegis.text.toString()) {
            binding.passwordConfirmEditTextLayout.error = "Confirm password does'nt match"
        } else {
            binding.passwordConfirmEditTextLayout.error = null
        }


        return (binding.nameRegis.text.toString().isNotEmpty() &&
                binding.emailRegis.text.toString().isNotEmpty() &&
                binding.passwordRegis.text.toString().isNotEmpty() &&
                binding.passwordRegisConfirm.text.toString().isNotEmpty() &&
                binding.passwordRegisConfirm.text.toString() == binding.passwordRegis.text.toString())
    }

    private fun setNewUser() {
        val email = binding.emailRegis.text.toString().trim()
        val password = binding.passwordRegis.text.toString().trim()
        val username = binding.nameRegis.text.toString().trim()


        loadingDialog.show()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        val createUser = hashMapOf(
                            "email" to email,
                            "username" to username,
                            "isVerified" to false,
                            "role" to "client"
                        )

                        db.collection("users").document(auth.currentUser!!.uid).set(createUser)
                            .addOnSuccessListener {

                                loadingDialog.dismiss()
                                successDialog()

                            }.addOnFailureListener {

                            Toast.makeText(
                                this,
                                "There is problem to store account!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }?.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "There is problem to verify account!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    loadingDialog.dismiss()
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        errorDialog()
                    } else {
                        Toast.makeText(
                            this,
                            "There is problem to create account!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun successDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success_register, null)
        val builder = AlertDialog.Builder(this@RegisterActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
            val intentToLogin = Intent(this, LoginActivity::class.java)
            intentToLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentToLogin)
            finish()
        }
    }

    private fun errorDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_error_register, null)
        val builder = AlertDialog.Builder(this@RegisterActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun loadingDialog(): AlertDialog {
        val view = layoutInflater.inflate(R.layout.dialog_loading, null)
        val builder = AlertDialog.Builder(this@RegisterActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun playAnimation() {
        // Button Back
        val btnBackFI = AnimationPackage.fadeIn(binding.btnRegisBack, 700)
        val btnBackX = AnimationPackage.translateX(binding.btnRegisBack, 700, -50f, 0f)

        // Banner
        val bannerFadeIn = AnimationPackage.fadeIn(binding.imgLogo, 700)
        val bannerMoveY = AnimationPackage.translateY(binding.imgLogo, 700, -50f, 0f)

        // Username
        val titleUsername = AnimationPackage.fadeIn(binding.tvRegisUser, 500)
        val titleUsernameX = AnimationPackage.translateX(binding.tvRegisUser, 500, -30f, 0f)

        val usernameTIL = AnimationPackage.fadeIn(binding.nameEditTextLayout, 500)
        val usernameTILX = AnimationPackage.translateX(binding.nameEditTextLayout, 500, -30f, 0f)

        // Email
        val titleEmail = AnimationPackage.fadeIn(binding.tvRegisEmail, 500)
        val titleEmailX = AnimationPackage.translateX(binding.tvRegisEmail, 500, -30f, 0f)

        val emailTIL = AnimationPackage.fadeIn(binding.emailEditTextLayout, 500)
        val emailTILX = AnimationPackage.translateX(binding.emailEditTextLayout, 500, -30f, 0f)

        // Password
        val titlePass = AnimationPackage.fadeIn(binding.tvRegisPass, 500)
        val titlePassX = AnimationPackage.translateX(binding.tvRegisPass, 500, -30f, 0f)

        val passTIL = AnimationPackage.fadeIn(binding.passwordEditTextLayout, 500)
        val passTILX = AnimationPackage.translateX(binding.passwordEditTextLayout, 500, -30f, 0f)

        // Password Confirm
        val titlePassConfirm = AnimationPackage.fadeIn(binding.tvRegisConPass, 500)
        val titlePassConfirmX = AnimationPackage.translateX(binding.tvRegisConPass, 500, -30f, 0f)

        val passConfirmTIL = AnimationPackage.fadeIn(binding.passwordConfirmEditTextLayout, 500)
        val passConfirmTILX =
            AnimationPackage.translateX(binding.passwordConfirmEditTextLayout, 500, -30f, 0f)

        // Register Button
        val btnRegister = AnimationPackage.fadeIn(binding.btnRegis, 500)

        // Note
        val titleNote = AnimationPackage.fadeIn(binding.note, 500)

        val bannerAnim = AnimatorSet().apply {
            play(btnBackFI).with(btnBackX)
            play(bannerFadeIn).with(bannerMoveY)
        }

        val usernameAnim = AnimatorSet().apply {
            play(titleUsername).with(titleUsernameX)
            play(usernameTIL).with(usernameTILX)
        }

        val emailAnim = AnimatorSet().apply {
            play(titleEmail).with(titleEmailX)
            play(emailTIL).with(emailTILX)
        }

        val passwordAnim = AnimatorSet().apply {
            play(titlePass).with(titlePassX)
            play(passTIL).with(passTILX)
        }

        val passwordConfirmAnim = AnimatorSet().apply {
            play(titlePassConfirm).with(titlePassConfirmX)
            play(passConfirmTIL).with(passConfirmTILX)
        }

        AnimatorSet().apply {
            playSequentially(
                bannerAnim,
                usernameAnim,
                emailAnim,
                passwordAnim,
                passwordConfirmAnim,
                titleNote,
                btnRegister
            )
            start()
        }


    }
}