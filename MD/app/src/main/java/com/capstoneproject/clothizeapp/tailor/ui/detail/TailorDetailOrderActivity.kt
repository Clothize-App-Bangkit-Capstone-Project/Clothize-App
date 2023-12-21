package com.capstoneproject.clothizeapp.tailor.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.api.response.Order
import com.capstoneproject.clothizeapp.client.ui.client.order.DetailOrderActivity
import com.capstoneproject.clothizeapp.databinding.ActivityTailorDetailOrderBinding
import com.capstoneproject.clothizeapp.tailor.ui.MainTailorActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TailorDetailOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTailorDetailOrderBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTailorDetailOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        db = Firebase.firestore

        val order = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DetailOrderActivity.ORDER_ID, Order::class.java)
        } else {
            intent.getParcelableExtra(DetailOrderActivity.ORDER_ID)
        }

        if (order != null) {
            loadContent(order)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadContent(order: Order) {
        val drawable = CircularProgressDrawable(this)
        drawable.setColorSchemeColors(R.color.brown_gold)
        drawable.centerRadius = 30f
        drawable.strokeWidth = 5f
        drawable.start()
        binding.apply {
            order.apply {
                tvTailorName.text = clientName
                tvTailorPhone.text = clientPhone
                tvStatus.text = if (status == "Rejected") {
                    if (isTailorRejected == true) {
                        "$status by tailor"
                    } else {
                        "$status by client"

                    }
                } else {
                    status
                }
                tvGender.text = gender
                tvService.text = service
                tvClothingColor.text = color
                tvClothingSize.text = size
                tvQty.text = "$qty pcs"
                tvEstimation.text = "$estimation days"
                tvPrice.text = formatCurrency(price?.toDouble() ?: 0.0, "IDR")
                val totalPrice = qty?.let { price?.times(it) }
                if (totalPrice != null) {
                    tvTotalPrice.text = formatCurrency(totalPrice.toDouble(), "IDR")
                }
                noteEditText.setText(comment)
                tvOrderDate.text = changeDateFormat(createdAt!!)

                Glide.with(this@TailorDetailOrderActivity)
                    .load(urlImg)
                    .placeholder(drawable)
                    .transition(
                        DrawableTransitionOptions.withCrossFade(
                            DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                        )
                    )
                    .into(imgUserModel)

                when (status) {
                    "Finished" -> setupFinished()

                    "Pending" -> setupPending()

                    "Rejected" -> setupRejected(order.isTailorRejected!!)

                    "Offer" -> setupOffer()

                    else -> setupOnProgress()
                }
            }

            btnApprove.setOnClickListener {
                val intentToApprove = Intent(this@TailorDetailOrderActivity, TailorApproveActivity::class.java)
                intentToApprove.putExtra(TailorApproveActivity.ORDER_ID, order)
                startActivity(intentToApprove)
            }

            btnRejected.setOnClickListener {
                val intentToRejected = Intent(this@TailorDetailOrderActivity, TailorRejectedActivity::class.java)
                intentToRejected.putExtra(TailorRejectedActivity.ORDER_ID, order)
                startActivity(intentToRejected)
            }

            btnFinish.setOnClickListener {
                val updateOrder = mapOf(
                    "status" to "Finished",
                )

                db.collection("orders").document(order.orderId!!).update(updateOrder)
                    .addOnSuccessListener {
                        successDialog()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@TailorDetailOrderActivity,
                            "There is problem to update order status!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

        }


    }

    private fun formatCurrency(amount: Double, currencyCode: String): String {
        val locale = Locale("en", "US") // Locale sesuai dengan pengaturan regional perangkat

        // Membuat instance NumberFormat untuk mata uang
        val currencyFormat = NumberFormat.getCurrencyInstance(locale)

        val formatSymbols = (currencyFormat as java.text.DecimalFormat).decimalFormatSymbols
        formatSymbols.currencySymbol = "IDR "

        // Mengatur mata uang berdasarkan kode mata uang
        val currency = Currency.getInstance(currencyCode)
        currencyFormat.currency = currency
        currencyFormat.maximumFractionDigits = 0
        currencyFormat.decimalFormatSymbols = formatSymbols

        // Memformat jumlah mata uang
        return currencyFormat.format(amount)
    }

    private fun changeDateFormat(date: Date): String {
        // Tentukan pola format waktu yang diinginkan
        val pattern = "dd-MM-yyyy"

        // Tentukan zona waktu Indonesia
        val indonesiaTimeZone = TimeZone.getTimeZone("Asia/Jakarta")

        // Buat objek SimpleDateFormat dengan pola dan zona waktu
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = indonesiaTimeZone

        // Format objek Date ke dalam string waktu
        return sdf.format(date)
    }


    private fun setupFinished() {
        binding.apply {
            btnApprove.visibility = View.GONE
            btnRejected.visibility = View.GONE
            btnFinish.visibility = View.GONE
        }
    }

    private fun setupPending() {
        binding.apply {
            boxNote.visibility = View.GONE
            boxFeedback.visibility = View.GONE
            boxPrice.visibility = View.GONE
            boxTotalPrice.visibility = View.GONE
            btnFinish.visibility = View.GONE
        }
    }

    private fun setupRejected(isTailorRejected : Boolean) {
        binding.apply {
            btnApprove.visibility = View.GONE
            btnRejected.visibility = View.GONE
            boxNote.visibility = View.GONE
            btnFinish.visibility = View.GONE
            if (isTailorRejected){
                boxPrice.visibility = View.GONE
                boxTotalPrice.visibility = View.GONE
            }else{
                boxPrice.visibility = View.VISIBLE
                boxTotalPrice.visibility = View.VISIBLE
            }
        }
    }

    private fun setupOffer() {
        binding.apply {
            boxNote.visibility = View.GONE
            btnApprove.visibility = View.GONE
            btnRejected.visibility = View.GONE
            btnFinish.visibility = View.GONE
        }
    }

    private fun setupOnProgress() {
        binding.apply {
            btnApprove.visibility = View.GONE
            btnRejected.visibility = View.GONE
            boxNote.visibility = View.GONE
            btnFinish.visibility = View.VISIBLE
        }
    }

    private fun successDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success_finish_order, null)
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