package com.capstoneproject.clothizeapp.client.ui.client.measurements

//import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.ui.client.MainClientActivity
import com.capstoneproject.clothizeapp.databinding.ActivityMeasurementBinding
import com.capstoneproject.clothizeapp.ml.Model
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
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MeasurementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMeasurementBinding
    private lateinit var viewModel: MeasurementViewModel
    private lateinit var loadingDialog: AlertDialog
    private lateinit var tfLite: Interpreter
    private var currentImageUri: Uri? = null
    private var typeClothes = ""
    private var gender = ""

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
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeasurementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().currentUser

        loadingDialog = loadingDialog()
        viewModel = obtainViewModel(this)

        // Fill entries to spinner
        val items = resources.getStringArray(R.array.type)
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

        binding.spType.adapter = adapter

        binding.spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long,
            ) {
                typeClothes = binding.spType.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@MeasurementActivity,
                    "Please select clothes type",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnBack.setOnClickListener {
            val intentToMainClient = Intent(this, MainClientActivity::class.java)
            finish()
            startActivity(intentToMainClient)
        }

        binding.btnOpenCamera.setOnClickListener {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }

        binding.btnGallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCalculate.setOnClickListener {
            calculateUserPhoto()
        }

        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rd_male -> gender = binding.rdMale.text.toString()
                R.id.rd_female -> gender = binding.rdFemale.text.toString()
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.userPhoto.setImageURI(it)
            viewModel.imageUri.postValue(it)
        }
    }

    private fun calculateUserPhoto() {
        if (checkFillOrNot()) {
            loadingDialog.show()

            CoroutineScope(Dispatchers.Main).launch {
                insertData()
            }
        } else {
            Toast.makeText(this, "Please fill all input before calculate!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun obtainViewModel(appCompatActivity: AppCompatActivity): MeasurementViewModel {
        val factory = MeasurementViewModelFactory.getInstance(appCompatActivity.applicationContext)
        return ViewModelProvider(
            appCompatActivity, factory
        )[MeasurementViewModel::class.java]
    }


    private fun checkFillOrNot(): Boolean {
        if (binding.heightEdt.text.toString()
                .isEmpty() || binding.heightEdt.text.toString() == "0"
        ) {
            binding.heightTIL.error = getString(R.string.not_fill)
            binding.heightEdt.error = null
        } else {
            binding.heightTIL.error = null
        }

        if (binding.weightEdt.text.toString()
                .isEmpty() || binding.weightEdt.text.toString() == "0"
        ) {
            binding.weightTIL.error = getString(R.string.not_fill)
            binding.weightEdt.error = null
        } else {
            binding.weightTIL.error = null
        }

        return (typeClothes != "Clothing type" &&
                gender != "" &&
                (binding.heightEdt.text.toString()
                    .isNotEmpty() && binding.heightEdt.text.toString() != "0") &&
                (binding.weightEdt.text.toString()
                    .isNotEmpty() && binding.weightEdt.text.toString() != "0") &&
                currentImageUri != null)
    }

    private fun loadingDialog(): AlertDialog {
        val view = layoutInflater.inflate(R.layout.dialog_loading, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }


    private fun insertData() {

        binding.apply {
            val weight = weightEdt.text.toString().toFloat()
            val height = heightEdt.text.toString().toFloat()
            val parsingSize = when (val size = modelCalculate(weight, height)) {
                "XXL" -> "X2L"
                "XXXL" -> "X3L"
                else -> {
                    size
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                val urlPhoto = saveImage()

                // initialize data
                val addMeasurement = hashMapOf(
                    "clothingSize" to parsingSize,
                    "clothingType" to typeClothes,
                    "gender" to gender,
                    "urlImg" to urlPhoto,
                    "userId" to user!!.uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "bodyGirth" to 0,
                    "bodyLength" to 0,
                    "chestGirth" to 0,
                    "shoulderWidth" to 0,
                )

                db.collection("measurements").add(addMeasurement)
                    .addOnSuccessListener {docs ->
                        loadingDialog.dismiss()
                        val intentToResult =
                            Intent(this@MeasurementActivity, CalculateResultActivity::class.java)
                        intentToResult.putExtra(CalculateResultActivity.MEASURE_ID, docs.id)
                        startActivity(intentToResult)
                    }.addOnFailureListener {
                        Toast.makeText(this@MeasurementActivity, "There is problem to calculate!", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

        }
    }

    private suspend fun saveImage(): String = withContext(Dispatchers.IO) {
        val storageRef = storage.reference
        val filename = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageRef = storageRef.child("images/measurement/${filename}.jpg")
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
                        this@MeasurementActivity,
                        "There is a problem to add the image!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        imageUrl
    }


    private fun modelCalculate(weight: Float, height: Float): String {
        val model = Model.newInstance(this)

        val maxWeight = 120
        val maxHeight = 200

        val heightParse = (height / maxHeight)
        val weightParse = (weight / maxWeight)

        val array2D = arrayOf(
            arrayOf(weightParse, heightParse)
        )

        val rows = array2D.size
        val cols = array2D[0].size

        val numElements = rows * cols

        val byteBuffer = ByteBuffer.allocateDirect(numElements * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                byteBuffer.putFloat(array2D[i][j])
            }
        }

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(rows, cols), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        val predictedLabel = getPredictedLabel(outputFeature0)

        model.close()

        return predictedLabel
    }

    private fun getPredictedLabel(outputFeature0: FloatArray): String {
        val maxIndex = outputFeature0.indices.maxBy { outputFeature0[it] }
        classLabels()

        val confidenceScore = outputFeature0[maxIndex ?: 0]
        return "${classLabels()[maxIndex ?: 0]}"
    }

    private fun classLabels(): Array<String> {
        return arrayOf(
            "L",
            "M",
            "S",
            "XL",
            "XXL",
            "XXXL",
        )
    }
}