package com.capstoneproject.clothizeapp.client.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.api.response.Measurement
import com.capstoneproject.clothizeapp.databinding.ItemHistoryFirstBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HistoryAdapter(options : FirestoreRecyclerOptions<Measurement>) :
    FirestoreRecyclerAdapter<Measurement, HistoryAdapter.HistoryViewHolder>(options) {
    class HistoryViewHolder(private var binding: ItemHistoryFirstBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemCount: Int, position: Int, history: Measurement) {

            binding.apply {
                val marginLayoutParams =
                    cardItemHistory.layoutParams as ViewGroup.MarginLayoutParams
                val marginBottomLast =
                    binding.root.resources.getDimensionPixelSize(R.dimen.margin_bottom)
                val marginBottomNormal =
                    binding.root.resources.getDimensionPixelSize(R.dimen.margin_bottom_normal)
                marginLayoutParams.bottomMargin =
                    if (position == itemCount - 1) marginBottomLast else marginBottomNormal
                cardItemHistory.layoutParams = marginLayoutParams
                tvBodySize.text = history.clothingSize
                tvTypeClothes.text = "${history.clothingType} (${history.gender})"
                tvSizeBodyCircum.text = "Body C : ${history.bodyGirth} cm"
                tvSizeChestCircum.text = "Chest C : ${history.chestGirth} cm"
                tvSizeBodyHeight.text = "Body L : ${history.bodyLength} cm"
                tvSizeShoulderLength.text = "Shoulder W : ${history.shoulderWidth} cm"
                createdAt.text = changeDateFormat(history.createdAt!!) + " WIB"
            }
        }
        private fun changeDateFormat(date: Date): String {
            // Tentukan pola format waktu yang diinginkan
            val pattern = "dd-MM-yyyy, HH:mm"

            // Tentukan zona waktu Indonesia
            val indonesiaTimeZone = TimeZone.getTimeZone("Asia/Jakarta")

            // Buat objek SimpleDateFormat dengan pola dan zona waktu
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.timeZone = indonesiaTimeZone

            // Format objek Date ke dalam string waktu
            return sdf.format(date)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =
            ItemHistoryFirstBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int, model: Measurement) {
        val history = getItem(position)
        holder.bind(itemCount, position, history)
    }




}