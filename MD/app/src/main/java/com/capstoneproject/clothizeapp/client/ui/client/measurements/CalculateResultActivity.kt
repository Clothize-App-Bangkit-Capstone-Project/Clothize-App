package com.capstoneproject.clothizeapp.client.ui.client.measurements

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.ui.client.MainClientActivity
import com.capstoneproject.clothizeapp.databinding.ActivityCalculateResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class CalculateResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculateResultBinding
    private lateinit var viewModel: MeasurementViewModel

    private lateinit var db: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var storage: FirebaseStorage

    private lateinit var measureId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculateResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }

    private fun init() {
        viewModel = obtainViewModel(this)
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        user = FirebaseAuth.getInstance().currentUser

        measureId = intent.getStringExtra(MEASURE_ID).toString()
        loadContent(measureId)

        binding.btnSave.setOnClickListener {
            saveMeasurement()
        }

        binding.btnCancel.setOnClickListener {
            deleteMeasurement()
        }
    }

    private fun saveMeasurement() {
        val measurement = mapOf(
            "chestGirth" to binding.tvChest.text.toString().toInt(),
            "shoulderWidth" to binding.tvShoulder.text.toString().toInt(),
            "bodyLength" to binding.tvPbody.text.toString().toInt(),
            "bodyGirth" to binding.tvLbody.text.toString().toInt(),
        )

        db.collection("measurements").document(measureId).update(measurement)
            .addOnSuccessListener {
                successDialog()
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "There is problem to add history",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun deleteMeasurement(){
        db.collection("measurements").document(measureId).delete()
            .addOnSuccessListener {
                val intentToMain = Intent(this, MainClientActivity::class.java)
                finish()
                startActivity(intentToMain)
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "There is problem to delete history",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadContent(measureId: String) {
        val drawable = CircularProgressDrawable(this)
        drawable.setColorSchemeColors(R.color.brown_gold)
        drawable.centerRadius = 30f
        drawable.strokeWidth = 5f
        drawable.start()

        binding.apply {

            db.collection("measurements").document(measureId).get()
                .addOnSuccessListener { docs ->
                    val type = docs.data?.get("clothingType").toString()
                    val size = docs.data?.get("clothingSize").toString()
                    val gender = docs.data?.get("gender").toString()
                    val url_photo = docs.data?.get("urlImg").toString()

                    val content = viewModel.getDetailSizeForUser(type, size, gender)

                    if (content != null) {
                        tvClothesType.text = type
                        tvClothesSize.text = content.size
                        tvClothesGender.text = gender
                        tvChest.text = content.chestGirth.toString()
                        tvLbody.text = content.bodyGirth.toString()
                        tvPbody.text = content.bodyLength.toString()
                        tvShoulder.text = content.shoulderWidth.toString()
                        Glide.with(this@CalculateResultActivity)
                            .load(url_photo)
                            .placeholder(drawable)
                            .transition(
                                DrawableTransitionOptions.withCrossFade(
                                    DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                                )
                            )
                            .into(userPhoto)
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        this@CalculateResultActivity,
                        "There is problem to fetch data",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

    private fun obtainViewModel(activity: Activity): MeasurementViewModel {
        val factory = MeasurementViewModelFactory.getInstance(activity.applicationContext)
        return ViewModelProvider(
            this, factory
        )[MeasurementViewModel::class.java]
    }


    private fun successDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success_add_history, null)
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

    companion object {
        const val SIZE = "size"
        const val GENDER = "gender"
        const val TYPE = "type"
        const val MEASURE_ID = "measure_id"
    }


    //    private fun loadSizeClothes(type: String, size: String, gender: String): DetailSize? {
//        val jsonFile = if (type == "t-shirts" || type == "shirts") {
//            resources.openRawResource(R.raw.tshirts)
//        } else {
//            resources.openRawResource(R.raw.jacket)
//        }
//
//        val builder = StringBuilder()
//        val reader = BufferedReader(InputStreamReader(jsonFile))
//        var line: String?
//        try {
//            while (reader.readLine().also { line = it } != null) {
//                builder.append(line)
//            }
//            val json = Gson().fromJson(builder.toString(), Size::class.java)
//            val sizes = if (gender == "male") json.maleSize else json.femaleSize
//            for (i in sizes.indices) {
//                if (sizes[i].size == size) {
//                    return sizes[i]
//                }
//            }
//        } catch (exception: IOException) {
//            exception.printStackTrace()
//        } catch (exception: JSONException) {
//            exception.printStackTrace()
//        }
//
//        return null
//    }
}