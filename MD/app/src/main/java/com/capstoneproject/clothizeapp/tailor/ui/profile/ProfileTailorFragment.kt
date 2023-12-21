package com.capstoneproject.clothizeapp.tailor.ui.profile

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.ui.custom_view.CustomEmailEditText
import com.capstoneproject.clothizeapp.client.ui.custom_view.CustomPasswordEditText
import com.capstoneproject.clothizeapp.client.ui.login.LoginActivity
import com.capstoneproject.clothizeapp.databinding.FragmentProfileTailorBinding
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPrefViewModel
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPreferences
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorPreferencesFactory
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.TailorSession
import com.capstoneproject.clothizeapp.tailor.data.local.preferences.dataTailorStore
import com.capstoneproject.clothizeapp.utils.getImageUri
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileTailorFragment : Fragment() {
    private lateinit var binding: FragmentProfileTailorBinding
    private lateinit var tailorPrefViewModel: TailorPrefViewModel
    private lateinit var tailorViewModel: ProfileTailorViewModel
    private lateinit var tailorSession: TailorSession
    private lateinit var loadingDialog: AlertDialog
    private var currentImageUri: Uri? = null
    private lateinit var geocoder: Geocoder
    private lateinit var latLng: LatLng

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var storage: FirebaseStorage

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(requireActivity(), "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileTailorBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        geocoder = Geocoder(requireActivity())
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        user = auth.currentUser

        loadingDialog = loadingDialog()
        tailorViewModel = obtainViewModel(requireActivity())
        val pref = TailorPreferences.getInstance(requireActivity().dataTailorStore)
        tailorPrefViewModel =
            ViewModelProvider(this, TailorPreferencesFactory(pref))[TailorPrefViewModel::class.java]

        tailorPrefViewModel.getSessionUser().observe(requireActivity()) { session ->
            if (session != null) {
                tailorSession = session
                showUser(session)
            }
        }

        binding.btnSave.setOnClickListener {
            configureSpecificElement("edit")
        }

        binding.btnCancel.setOnClickListener {
            configureSpecificElement("cancel")
        }

        binding.tvChangePass.setOnClickListener {
            changeAccountDialog(tailorSession)
        }

        binding.tvChangePhoto.setOnClickListener {
            pickerImageDialog()
        }
    }

    private fun obtainViewModel(activity: Activity): ProfileTailorViewModel {
        val factory = ProfileTailorViewModelFactory.getInstance(activity.applicationContext)
        return ViewModelProvider(
            this, factory
        )[ProfileTailorViewModel::class.java]
    }

    private fun showUser(tailorSession: TailorSession) {
        val drawable = CircularProgressDrawable(requireActivity())
        drawable.setColorSchemeColors(R.color.brown_gold)
        drawable.centerRadius = 30f
        drawable.strokeWidth = 5f
        drawable.start()
        binding.apply {
            tvUsername.text = tailorSession.username
            nameEditText.setText(tailorSession.storeName)
            phoneEditText.setText(tailorSession.phone)
            latLng = LatLng(tailorSession.location.latitude, tailorSession.location.longitude)
            addressEditText.setText(getLocationWithLatLng(latLng))
            Glide.with(requireActivity())
                .load(tailorSession.urlPhoto)
                .placeholder(drawable)
                .transition(
                    DrawableTransitionOptions.withCrossFade(
                        DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                    )
                )
                .into(avatar)
        }
    }

    private fun getLocationWithLatLng(latLng: LatLng): String {
        var location = ""
        val latitude = latLng.latitude
        val longitude = latLng.longitude

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    location = addresses[0].getAddressLine(0)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), "Error Load Location!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        return location
    }

    private fun getLatLngWithAddress(locationName: String): LatLng {
        var location: LatLng = LatLng(0.0, 0.0)
        val latitude = latLng.latitude
        val longitude = latLng.longitude

        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    location = LatLng(addresses[0].latitude, addresses[0].longitude)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), "Error Load Location!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        return location
    }

    private fun configureSpecificElement(tag: String) {
        if (tag == "edit") {
            if (binding.btnSave.text == "Save") {
                hideElement()
                updateUserData()
            } else {
                showElement()
            }
        } else {
            if (binding.btnCancel.text == "Cancel") {
                hideElement()
            } else {
                tailorPrefViewModel.logoutUser()
                val intentToLogin = Intent(activity, LoginActivity::class.java)
                activity?.finish()
                startActivity(intentToLogin)
            }
        }
    }

    private fun showElement() {
        binding.tvChangePhoto.visibility = View.VISIBLE
        binding.btnSave.text = "Save"
        binding.btnCancel.text = "Cancel"

        binding.nameEditText.isFocusable = true
        binding.nameEditText.isFocusableInTouchMode = true

        binding.phoneEditText.isFocusable = true
        binding.phoneEditText.isFocusableInTouchMode = true

        binding.addressEditText.isFocusable = true
        binding.addressEditText.isFocusableInTouchMode = true
    }


    private fun hideElement() {
        binding.tvChangePhoto.visibility = View.GONE
        binding.btnSave.text = "Edit"
        binding.btnCancel.text = "Logout"

        binding.nameEditText.isFocusable = false
        binding.nameEditText.isFocusableInTouchMode = false

        binding.phoneEditText.isFocusable = false
        binding.phoneEditText.isFocusableInTouchMode = false

        binding.addressEditText.isFocusable = false
        binding.addressEditText.isFocusableInTouchMode = false
    }

    private fun updateUserData() {
        val storeName = binding.nameEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()
        val address = binding.addressEditText.text.toString()
        val latLon = getLatLngWithAddress(address)

        CoroutineScope(Dispatchers.Main).launch {
            loadingDialog.show()

            val urlPhoto = if (currentImageUri != null) {
                saveImage()
            } else {
                tailorSession.urlPhoto.ifEmpty {
                    ""
                }
            }

            val updateUser = mapOf(
                "storeName" to storeName,
                "location" to GeoPoint(latLon.latitude, latLon.longitude),
                "phone" to phone,
                "storeImg" to urlPhoto
            )

            db.collection("tailors").document(user!!.uid).update(updateUser)
                .addOnSuccessListener {
                    loadingDialog.dismiss()
                    tailorSession = TailorSession(
                        username = tailorSession.username,
                        email = tailorSession.email,
                        storeName = storeName,
                        phone = phone,
                        location = GeoPoint(latLon.latitude, latLon.longitude),
                        urlPhoto = urlPhoto,
                    )

                    tailorPrefViewModel.saveSessionUser(tailorSession)
                    successDialog()
                }.addOnFailureListener {
                    loadingDialog.dismiss()
                    errorDialog()
                }
        }


    }

    private suspend fun saveImage(): String = withContext(Dispatchers.IO) {
        val storageRef = storage.reference
        val filename = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageRef = storageRef.child("images/tailor/${filename}.jpg")
        var imageUrl = ""

        currentImageUri?.let {
            try {
                // Gunakan async untuk membuat saveImage menunggu proses upload
                val uploadTask = async {
                    imageRef.putFile(it).await()
                }

                uploadTask.await() // Menunggu proses upload selesai

                val downloadUrl = imageRef.downloadUrl.await()
                imageUrl = downloadUrl.toString()
                Log.d("TAG", "saveImage: $imageUrl")

            } catch (exception: Exception) {
                // Handle kesalahan di sini
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireActivity(),
                        "There is a problem to add the image!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        imageUrl
    }

    private fun changeAccountDialog(tailorSession: TailorSession) {
        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val email = view.findViewById<CustomEmailEditText>(R.id.emailEditText)
        val currentPass =
            view.findViewById<CustomPasswordEditText>(R.id.passEditText)
        val newPass =
            view.findViewById<CustomPasswordEditText>(R.id.newPassEditText)

        email.setText(tailorSession.email)


        view.findViewById<AppCompatButton>(R.id.btn_replace).setOnClickListener {
            changeAccount(
                email.text.toString(),
                currentPass.text.toString(),
                newPass.text.toString(),
                tailorSession,
                view
            )
        }

        view.findViewById<AppCompatButton>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun changeAccount(
        newMail: String,
        oldPassword: String,
        newPassword: String,
        session: TailorSession,
        view: View,
    ) {
        val user = auth.currentUser

        if (checkFillOrNot(view)) {
            val credential = EmailAuthProvider.getCredential(session.email, oldPassword)
            loadingDialog.show()
            user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (newPassword.isEmpty()) {

                        CoroutineScope(Dispatchers.Main).launch {
                            val isExist = checkExistEmail(newMail)

                            if (!isExist) {
                                user.verifyBeforeUpdateEmail(newMail)
                                    .addOnCompleteListener { status ->
                                        loadingDialog.dismiss()
                                        if (status.isSuccessful) {
                                            val updateUser = mapOf(
                                                "email" to newMail,
                                            )

                                            db.collection("users").document(user!!.uid)
                                                .update(updateUser).addOnSuccessListener {
                                                    session.email = newMail
                                                    successDialog()
                                                }.addOnFailureListener {
                                                    errorDialog()
                                                }


                                        } else {
                                            errorDialog()
                                        }
                                    }.addOnFailureListener {
                                        loadingDialog.dismiss()
                                        errorDialog()
                                    }
                            } else {
                                loadingDialog.dismiss()
                                errorDialog()
                            }
                        }

                    } else {
                        user.updatePassword(newPassword).addOnCompleteListener { task ->
                            loadingDialog.dismiss()
                            if (task.isSuccessful) {
                                successDialog()
                            } else {
                                errorDialog()
                            }
                        }.addOnFailureListener {
                            loadingDialog.dismiss()
                            errorDialog()
                        }
                    }
                } else {
                    loadingDialog.dismiss()
                    errorDialog()
                }
            }?.addOnFailureListener {
                loadingDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Your password doesn't match!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private suspend fun checkExistEmail(email: String): Boolean = withContext(Dispatchers.IO) {
        val isExist: Boolean
        val test = db.collection("users").whereEqualTo("email", email).get().await()
        isExist = test.documents.size > 0
        isExist
    }

    private fun loadingDialog(): AlertDialog {
        val view = layoutInflater.inflate(R.layout.dialog_loading, null)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun successDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success_change_profile, null)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun errorDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_error_change_profile, null)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun pickerImageDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        with(alertDialogBuilder) {
            setTitle("Picker Imager")
            setMessage("Choose resource to pick your image!")
            setCancelable(true)
            setPositiveButton("Gallery") { _, _ ->
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            setNegativeButton("Camera") { _, _ ->
                currentImageUri = getImageUri(requireActivity())
                launcherIntentCamera.launch(currentImageUri)
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.avatar.setImageURI(it)
            tailorViewModel.imageUri.postValue(it)
        }
    }

    private fun checkFillOrNot(view: View): Boolean {
        val fillEmail = view.findViewById<CustomEmailEditText>(R.id.emailEditText)
        val fillOldPass = view.findViewById<CustomPasswordEditText>(R.id.passEditText)
        val fillNewPass = view.findViewById<CustomPasswordEditText>(R.id.newPassEditText)

        val fillEmailTIL = view.findViewById<TextInputLayout>(R.id.emailEditTextLayout)
        val fillOldPassTIL = view.findViewById<TextInputLayout>(R.id.passwordEditTextLayout)
        val fillNewPassTIL = view.findViewById<TextInputLayout>(R.id.newPasswordEditTextLayout)


        if (fillEmail.text.toString().isEmpty()) {
            fillEmailTIL.error = getString(R.string.not_fill)
            fillEmail.error = null
        } else {
            fillEmailTIL.error = null
        }

        if (fillOldPass.text.toString().isEmpty()) {
            fillOldPassTIL.error = getString(R.string.not_fill)
            fillOldPass.error = null
        } else {
            fillOldPassTIL.error = null
        }

        if (fillOldPass.text.toString().isEmpty()) {
            fillNewPassTIL.error = getString(R.string.not_fill)
            fillNewPass.error = null
        } else {
            fillNewPassTIL.error = null
        }

        return if (fillNewPass.text.toString().isEmpty()) {
            (fillEmail.text.toString().isNotEmpty() && fillOldPass.text.toString().isNotEmpty())
        } else {
            (fillEmail.text.toString().isNotEmpty() && fillOldPass.text.toString()
                .isNotEmpty() && fillNewPass.text.toString().isNotEmpty())
        }


    }

}