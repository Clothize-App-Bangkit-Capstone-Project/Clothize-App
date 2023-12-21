package com.capstoneproject.clothizeapp.client.ui.client.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPrefViewModel
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferences
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.ClientPreferencesFactory
import com.capstoneproject.clothizeapp.client.data.local.preferences.client.dataStore
import com.capstoneproject.clothizeapp.client.ui.client.MainClientActivity
import com.capstoneproject.clothizeapp.client.ui.client.order.OrderViewModel
import com.capstoneproject.clothizeapp.client.ui.client.order.OrderViewModelFactory
import com.capstoneproject.clothizeapp.databinding.ActivityOrderFormBinding
import com.capstoneproject.clothizeapp.utils.getImageUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

class OrderFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderFormBinding
    private lateinit var clientPrefViewModel: ClientPrefViewModel
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var loadingDialog: AlertDialog
    private var spService = ""
    private var spSize = ""
    private var spColor = ""
    private var gender = ""
    private var currentImageUri: Uri? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var user: FirebaseUser? = null

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
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().currentUser

        loadingDialog = loadingDialog()
        orderViewModel = obtainViewModel(this)
        val pref = ClientPreferences.getInstance(application.dataStore)
        clientPrefViewModel =
            ViewModelProvider(this, ClientPreferencesFactory(pref))[ClientPrefViewModel::class.java]

        spinnerService()
        spinnerSize()
        spinnerColor()

        binding.apply {

            clientPrefViewModel.getSessionUser().observe(this@OrderFormActivity) { session ->
                session?.apply {
                    orderOrderName.text = fullName.ifEmpty { "Please update your profile!" }
                    orderOrderPhone.text = phone.ifEmpty { "Please update your profile!" }
                }

            }

            rgGenderOrder.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rd_male -> gender = rdMale.text.toString()
                    R.id.rd_female -> gender = rdFemale.text.toString()
                }
            }

            orderBtnCancel.setOnClickListener {
                finish()
            }

            orderBtnImage.setOnClickListener {
                pickerImageDialog()
            }

            orderBtnOrder.setOnClickListener {
                createOrder()
            }
        }


    }

    private fun obtainViewModel(activity: Activity): OrderViewModel {
        val factory = OrderViewModelFactory.getInstance(activity.applicationContext)
        return ViewModelProvider(
            this, factory
        )[OrderViewModel::class.java]
    }

    private fun spinnerService() {
        // Fill entries to spinner
        val items = resources.getStringArray(R.array.service)
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_items, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup,
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                if (position == 0) {
                    view.visibility = View.GONE
                } else {
                    view.visibility = View.VISIBLE
                }
                return view
            }
        }

        adapter.setDropDownViewResource(R.layout.spinner_items)

        binding.orderOrderService.adapter = adapter

        binding.orderOrderService.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    if (binding.orderOrderService.selectedItem.toString() != "Service") {
                        spService = binding.orderOrderService.selectedItem.toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@OrderFormActivity,
                        "Please select clothes type",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun spinnerSize() {
        // Fill entries to spinner
        val items = resources.getStringArray(R.array.size)
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_items, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup,
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                if (position == 0) {
                    view.visibility = View.GONE
                } else {
                    view.visibility = View.VISIBLE
                }
                return view
            }
        }

        adapter.setDropDownViewResource(R.layout.spinner_items)

        binding.orderOrderSize.adapter = adapter

        binding.orderOrderSize.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    if (binding.orderOrderSize.selectedItem.toString() != "Size") {
                        spSize = binding.orderOrderSize.selectedItem.toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@OrderFormActivity,
                        "Please select clothes type",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun spinnerColor() {
        // Fill entries to spinner
        val items = resources.getStringArray(R.array.color)
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_items, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup,
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                if (position == 0) {
                    view.visibility = View.GONE
                } else {
                    view.visibility = View.VISIBLE
                }
                return view
            }
        }

        adapter.setDropDownViewResource(R.layout.spinner_items)

        binding.orderOrderColor.adapter = adapter

        binding.orderOrderColor.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long,
                ) {
                    if (binding.orderOrderColor.selectedItem.toString() != "Color") {
                        spColor = binding.orderOrderColor.selectedItem.toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@OrderFormActivity,
                        "Please select clothes type",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun pickerImageDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle("Picker Imager")
            setMessage("Choose resource to pick your model!")
            setCancelable(true)
            setPositiveButton("Gallery") { _, _ ->
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            setNegativeButton("Camera") { _, _ ->
                currentImageUri = getImageUri(this@OrderFormActivity)
                launcherIntentCamera.launch(currentImageUri)
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.apply {
                tvPlaceholderImg.visibility = View.GONE
                imgModel.setImageURI(it)
            }
            orderViewModel.imageUri.postValue(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOrder() {

        val tailor = intent.getStringExtra(TAILOR)
        val tailorPhone = intent.getStringExtra(TAILOR_PHONE)
        val tailorId = intent.getStringExtra(TAILOR_ID)
        val clientName = binding.orderOrderName.text.toString()
        val clientPhone = binding.orderOrderPhone.text.toString()

        if (checkFillOrNot()) {
            if (tailor != null) {
                loadingDialog.show()
                binding.apply {
                    CoroutineScope(Dispatchers.Main).launch {
                        val urlPhoto = saveImage()

                        val addOrder = hashMapOf(
                            "clientName" to clientName,
                            "clientPhone" to clientPhone,
                            "tailorName" to tailor,
                            "tailorPhone" to tailorPhone,
                            "gender" to gender,
                            "service" to spService,
                            "size" to spSize,
                            "color" to spColor,
                            "qty" to orderQuantity.text.toString().toInt(),
                            "estimation" to orderEstimated.text.toString().toInt(),
                            "status" to "Pending",
                            "comment" to "",
                            "price" to 0,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "urlImg" to urlPhoto,
                            "isTailorRejected" to false,
                            "clientId" to user!!.uid,
                            "tailorId" to tailorId,
                        )

                        db.collection("orders").add(addOrder)
                            .addOnSuccessListener { docs ->
                                loadingDialog.dismiss()
                                successDialog()
                            }.addOnFailureListener {
                                loadingDialog.dismiss()
                                Toast.makeText(
                                    this@OrderFormActivity,
                                    "There is problem to add order!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please input all correctly!", Toast.LENGTH_SHORT).show()
        }
    }


    private suspend fun saveImage(): String = withContext(Dispatchers.IO) {
        val storageRef = storage.reference
        val filename = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageRef = storageRef.child("images/order/${filename}.jpg")
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
                        this@OrderFormActivity,
                        "There is a problem to add the image!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        imageUrl
    }

    private fun checkFillOrNot(): Boolean {
        binding.apply {
            return (
                    orderOrderName.text.toString().isNotEmpty() &&
                            orderOrderPhone.text.toString().isNotEmpty() &&
                            orderOrderPhone.text.toString().isNotEmpty() &&
                            gender != "" && spService != "" && spSize != "" &&
                            spColor != "" && orderQuantity.text.toString().isNotEmpty() &&
                            orderEstimated.text.toString().isNotEmpty() && currentImageUri != null
                    )
        }
    }

    private fun successDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success_add_order, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
            val intentToMain = Intent(this, MainClientActivity::class.java)
            finish()
            startActivity(intentToMain)
        }
    }

    private fun loadingDialog(): AlertDialog {
        val view = layoutInflater.inflate(R.layout.dialog_loading, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }


    companion object {
        const val TAILOR = "tailor"
        const val TAILOR_PHONE = "tailor_phone"
        const val TAILOR_ID = "tailor_id"
    }
}