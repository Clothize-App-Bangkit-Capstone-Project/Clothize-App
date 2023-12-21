package com.capstoneproject.clothizeapp.client.ui.login

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPrefViewModel
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferences
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferencesFactory
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientSession
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.dataStore
import com.capstoneproject.clothizeapp.client.ui.client.MainClientActivity
import com.capstoneproject.clothizeapp.client.ui.register.RegisterActivity
import com.capstoneproject.clothizeapp.databinding.ActivityLoginBinding
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPrefViewModel
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPreferences
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPreferencesFactory
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorSession
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.dataTailorStore
import com.capstoneproject.clothizeapp.tailor.ui.MainTailorActivity
import com.capstoneproject.clothizeapp.utils.AnimationPackage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var clientPref: ClientPreferences
    private lateinit var tailorPref: TailorPreferences
    private lateinit var clientPrefViewModel: ClientPrefViewModel
    private lateinit var tailorPrefViewModel: TailorPrefViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: AlertDialog

    private lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()
        init()


    }

    private fun init() {
        auth = Firebase.auth
        db = Firebase.firestore

        clientPref = ClientPreferences.getInstance(application.dataStore)
        clientPrefViewModel =
            ViewModelProvider(
                this,
                ClientPreferencesFactory(clientPref)
            )[ClientPrefViewModel::class.java]

        tailorPref = TailorPreferences.getInstance(application.dataTailorStore)
        tailorPrefViewModel =
            ViewModelProvider(
                this,
                TailorPreferencesFactory(tailorPref)
            )[TailorPrefViewModel::class.java]

        binding.tvIntentRegis.setOnClickListener {
            val intentToRegister = Intent(this, RegisterActivity::class.java)
            finish()
            startActivity(intentToRegister)
        }

        binding.btnLogin.setOnClickListener {
            if (checkFillOrNot()) {
                authenticate()
            }
        }

        dialog = loadingDialog()

    }

    private fun authenticate() {
        val emailUsername = binding.emailUsernameEdt.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()


        dialog.show()
        auth.signInWithEmailAndPassword(emailUsername, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser!!
                    if (user.isEmailVerified) {
                        db.collection("users").document(user.uid).get()
                            .addOnSuccessListener { doc ->
                                val updateUser = mapOf(
                                    "isVerified" to true,
                                )

                                if (doc.data?.get("role") == "client") {

                                    db.collection("clients").document(user.uid).get()
                                        .addOnSuccessListener { documents ->
                                            CoroutineScope(Dispatchers.Main).launch {
                                                if (!documents.exists()) {
                                                    checkAndInsertNewClient()

                                                }

                                                db.collection("users").document(user.uid)
                                                    .update(updateUser)
                                                    .addOnSuccessListener {
                                                        val userSession = ClientSession(
                                                            email = doc.data?.get("email")
                                                                .toString(),
                                                            username = doc.data?.get("username")
                                                                .toString(),
                                                            fullName = if (documents.data != null) documents.data?.get(
                                                                "fullname"
                                                            ).toString() else "Client",
                                                            phone = if (documents.data != null) documents.data?.get(
                                                                "phone"
                                                            ).toString() else "Client",
                                                            address = if (documents.data != null) documents.data?.get(
                                                                "address"
                                                            ).toString() else "Client",
                                                            urlPhoto = if (documents.data != null) documents.data?.get(
                                                                "avatar"
                                                            )
                                                                .toString() else "https://firebasestorage.googleapis.com/v0/b/clothize-app-6879d.appspot.com/o/avatar%2Fuser%20(1).png?alt=media&token=f737a87d-3b5d-422e-9bc2-c8dabfa7b08f",
                                                        )

                                                        clientPrefViewModel.saveSessionUser(
                                                            userSession
                                                        )
                                                        dialog.dismiss()
                                                        val intentToMain =
                                                            Intent(
                                                                this@LoginActivity,
                                                                MainClientActivity::class.java
                                                            )
                                                        finish()
                                                        startActivity(intentToMain)
                                                    }.addOnFailureListener {
                                                        dialog.dismiss()
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "Your account does not exist!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }

                                        }


                                } else {


                                    db.collection("tailors").document(user.uid).get()
                                        .addOnSuccessListener { documents ->
                                            CoroutineScope(Dispatchers.Main).launch {
                                                if (!documents.exists()) {
                                                    checkAndInsertNewTailor()
                                                }

                                                db.collection("users").document(user.uid)
                                                    .update(updateUser)
                                                    .addOnSuccessListener {
                                                        val userSession = TailorSession(
                                                            email = doc.data?.get("email")
                                                                .toString(),
                                                            username = doc.data?.get("username")
                                                                .toString(),
                                                            storeName = if (documents.data != null) documents.data?.get(
                                                                "storeName"
                                                            ).toString() else "Tailor",
                                                            phone = if (documents.data != null) documents.data?.get(
                                                                "phone"
                                                            ).toString() else "",
                                                            urlPhoto = if (documents.data != null) documents.data?.get(
                                                                "storeImg"
                                                            )
                                                                .toString() else "https://firebasestorage.googleapis.com/v0/b/clothize-app-6879d.appspot.com/o/avatar%2Fstore.png?alt=media&token=827737ad-861f-4d81-a423-872ed7a1b34c",
                                                            location = if (documents.data != null) {
                                                                val location =
                                                                    documents.getGeoPoint("location")
                                                                GeoPoint(
                                                                    location?.latitude
                                                                        ?: 0.0,
                                                                    location?.longitude
                                                                        ?: 0.0
                                                                )
                                                            } else {
                                                                GeoPoint(
                                                                    0.0,
                                                                    0.0
                                                                )
                                                            }

                                                        )

                                                        tailorPrefViewModel.saveSessionUser(
                                                            userSession
                                                        )
                                                        dialog.dismiss()
                                                        val intentToTailor =
                                                            Intent(
                                                                this@LoginActivity,
                                                                MainTailorActivity::class.java
                                                            )
                                                        finish()
                                                        startActivity(intentToTailor)


                                                    }.addOnFailureListener {
                                                        dialog.dismiss()
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "Your account does not exist!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }


                                        }

                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Please verify your email!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    dialog.dismiss()
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        errorDialog()
                    } else {
                        Toast.makeText(
                            this,
                            "There is problem to login account!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            }
    }


    private suspend fun checkAndInsertNewClient() = withContext(Dispatchers.IO) {
        db.collection("clients").document(user.uid).get()
            .addOnSuccessListener { documents ->
                if (!documents.exists()) {
                    val addClient = hashMapOf(
                        "fullname" to "Client",
                        "phone" to "",
                        "address" to "",
                        "avatar" to "https://firebasestorage.googleapis.com/v0/b/clothize-app-6879d.appspot.com/o/avatar%2Fuser%20(1).png?alt=media&token=f737a87d-3b5d-422e-9bc2-c8dabfa7b08f"
                    )

                    // insert new client
                    db.collection("clients").document(user.uid).set(addClient)
                        .addOnFailureListener {
                            Toast.makeText(
                                this@LoginActivity,
                                "Failed to add client!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this@LoginActivity,
                    "Failed to add new client!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }

    private suspend fun checkAndInsertNewTailor() = withContext(Dispatchers.IO) {
        val addTailor = mapOf(
            "storeName" to "Tailor",
            "phone" to "",
            "location" to GeoPoint(0.0, 0.0),
            "storeImg" to "https://firebasestorage.googleapis.com/v0/b/clothize-app-6879d.appspot.com/o/avatar%2Fstore.png?alt=media&token=827737ad-861f-4d81-a423-872ed7a1b34c",
            "description" to "",
        )

        db.collection("tailors").document(user.uid)
            .set(addTailor).addOnFailureListener {
                Toast.makeText(
                    this@LoginActivity,
                    "There is problem to add Tailor!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun checkFillOrNot(): Boolean {

        if (binding.emailUsernameEdt.text.toString().isEmpty()) {
            binding.emailUsernameTIL.error = getString(R.string.not_fill)
            binding.emailUsernameEdt.error = null
        } else if (binding.emailUsernameEdt.error != null) {
            binding.emailUsernameTIL.isErrorEnabled = false
            binding.emailUsernameTIL.error = null
        } else {
            binding.emailUsernameTIL.isErrorEnabled = false
            binding.emailUsernameTIL.clearFocus()
            binding.passTIL.requestFocus()
        }

        if (binding.passwordEditText.text.toString().isEmpty()) {
            binding.passTIL.error = getString(R.string.not_fill)
            binding.passwordEditText.error = null
        } else if (binding.passwordEditText.error != null) {
            binding.passTIL.isErrorEnabled = false
            binding.passTIL.error = null
        } else {

            binding.passTIL.isErrorEnabled = false
            binding.passTIL.clearFocus()
        }


        return !((binding.emailUsernameTIL.error != null && binding.emailUsernameEdt.error == null)
                || (binding.passTIL.error != null || binding.passwordEditText.error != null))
    }

    private fun errorDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_error_login, null)
        val builder = AlertDialog.Builder(this@LoginActivity)
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
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun playAnimation() {
        // Banner
        val bannerFadeIn = AnimationPackage.fadeIn(binding.imgLogo, 700)
        val bannerMoveY = AnimationPackage.translateY(binding.imgLogo, 700, -50f, 0f)

        // Email
        val titleEmailUsername = AnimationPackage.fadeIn(binding.tvEmail, 500)
        val titleEmailUsernameX = AnimationPackage.translateX(binding.tvEmail, 500, -30f, 0f)

        val emailUsernameTIL = AnimationPackage.fadeIn(binding.emailUsernameTIL, 500)
        val emailUsernameTILX = AnimationPackage.translateX(binding.emailUsernameTIL, 500, -30f, 0f)

        // Password
        val titlePassword = AnimationPackage.fadeIn(binding.tvPass, 500)
        val titlePasswordX = AnimationPackage.translateX(binding.tvPass, 500, -30f, 0f)

        val passwordTIL = AnimationPackage.fadeIn(binding.passTIL, 500)
        val passwordTILX = AnimationPackage.translateX(binding.passTIL, 500, -30f, 0f)

        // Login Button
        val btnLogin = AnimationPackage.fadeIn(binding.btnLogin, 500)

        // boxToRegister
        val boxToRegister = AnimationPackage.fadeIn(binding.boxToRegister, 500)
        val boxToRegisterY = AnimationPackage.translateY(binding.boxToRegister, 500, 30f, 0f)

        val bannerAnim = AnimatorSet().apply {
            play(bannerFadeIn).with(bannerMoveY)
        }

        val emailAnim = AnimatorSet().apply {
            play(titleEmailUsername).with(titleEmailUsernameX)
            play(emailUsernameTIL).with(emailUsernameTILX)
        }
        val passwordAnim = AnimatorSet().apply {
            play(titlePassword).with(titlePasswordX)
            play(passwordTIL).with(passwordTILX)
        }

        val boxToRegisterAnim = AnimatorSet().apply {
            play(boxToRegister).with(boxToRegisterY)
        }


        AnimatorSet().apply {
            playSequentially(
                bannerAnim,
                emailAnim,
                passwordAnim,
                btnLogin,
                boxToRegisterAnim
            )
            start()
        }


    }
}