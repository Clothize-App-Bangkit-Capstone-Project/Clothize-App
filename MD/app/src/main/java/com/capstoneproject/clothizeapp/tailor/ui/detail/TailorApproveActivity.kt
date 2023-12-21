package com.capstoneproject.clothizeapp.tailor.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.api.response.Order
import com.capstoneproject.clothizeapp.databinding.ActivityTailorApproveBinding
import com.capstoneproject.clothizeapp.tailor.ui.MainTailorActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TailorApproveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTailorApproveBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTailorApproveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        db = Firebase.firestore

        val order = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ORDER_ID, Order::class.java)
        } else {
            intent.getParcelableExtra(ORDER_ID)
        }


        binding.apply {
            btnApproveBack.setOnClickListener {
                finish()
            }

            btnAccept.setOnClickListener {
                if (checkFillOrNot()){
                    val comment = commentEditText.text.toString()
                    val price = priceEditText.text.toString().toInt()

                    val updateOrder = mapOf(
                        "comment" to comment,
                        "price" to price,
                        "status" to "Offer",
                    )

                    if (order != null){
                        db.collection("orders").document(order.orderId!!).update(updateOrder)
                            .addOnSuccessListener {
                                successDialog()
                            }.addOnFailureListener {
                                Toast.makeText(this@TailorApproveActivity, "There is problem to update order status!", Toast.LENGTH_SHORT).show()
                            }
                    }

                }
            }
        }
    }

    private fun checkFillOrNot(): Boolean {
        binding.apply {
            if (commentEditText.text.toString().isEmpty()) {
                commentEditTextLayout.error = getString(R.string.not_fill)
            } else {
                commentEditTextLayout.error = null
            }

            if (priceEditText.text.toString().isEmpty()) {
                priceEditTextLayout.error = getString(R.string.not_fill)
            } else {
                priceEditTextLayout.error = null
            }

            return (commentEditText.text.toString().isNotEmpty() &&
                    priceEditText.text.toString().isNotEmpty())

        }
    }

    private fun successDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success_update_order, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
            val intentToMain = Intent(this, MainTailorActivity::class.java)
            finish()
            startActivity(intentToMain)
        }
    }

    companion object {
        const val ORDER_ID = "id"
    }
}