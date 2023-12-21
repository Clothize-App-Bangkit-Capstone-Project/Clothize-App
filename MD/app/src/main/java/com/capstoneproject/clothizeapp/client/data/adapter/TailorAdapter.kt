package com.capstoneproject.clothizeapp.client.data.adapter

import android.content.Intent
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.api.response.Tailor
import com.capstoneproject.clothizeapp.client.ui.client.detail.DetailActivity
import com.capstoneproject.clothizeapp.databinding.ItemTailorBinding
import com.google.android.gms.maps.model.LatLng

class TailorAdapter() :
    ListAdapter<Tailor, TailorAdapter.TailorViewHolder>(DIFF_CALLBACK) {

    private var originalList = mutableListOf<Tailor>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TailorViewHolder {
        val binding = ItemTailorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TailorViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TailorViewHolder,
        position: Int,
    ) {
        val tailor = getItem(position)
        holder.bind(tailor)
    }


    class TailorViewHolder(private val binding: ItemTailorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val geocoder = Geocoder(itemView.context)
        fun bind(tailor: Tailor) {
            val drawable = CircularProgressDrawable(itemView.context)
            drawable.setColorSchemeColors(R.color.brown_gold)
            drawable.centerRadius = 30f
            drawable.strokeWidth = 5f
            drawable.start()
            binding.apply {
                Glide.with(itemView)
                    .load(tailor.storeImg)
                    .placeholder(drawable)
                    .transition(
                        DrawableTransitionOptions.withCrossFade(
                            DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                        )
                    )
                    .into(rvPhotoTailor)
                rvNameTailor.text = tailor.storeName
                rvLocationTailor.text = getLocationName(
                    LatLng(
                        tailor.location!!.latitude!!,
                        tailor.location.longitude!!
                    )
                )
                cardItemTailor.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.TAILOR_NAME, tailor.storeName)
                    intent.putExtra(DetailActivity.TAILOR_LATITUDE, tailor.location.latitude)
                    intent.putExtra(DetailActivity.TAILOR_LONGITUDE, tailor.location.longitude)
                    binding.cardItemTailor.context.startActivity(intent)
                }
            }

        }

        private fun getLocationName(location: LatLng): String {
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        return "${addresses[0].subAdminArea}"
                    } else {
                        Toast.makeText(itemView.context, "Location not found", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(itemView.context, "Error Load Location!", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

            return "Not found"
        }
    }

    fun searchTailor(query: String) {
        val filteredList = originalList.filter { model ->
            model.storeName!!.contains(query, ignoreCase = true)
        }
        submitList(filteredList)
    }

    fun setData(list: MutableList<Tailor>) {
        originalList = list
        submitList(originalList)
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tailor>() {
            override fun areItemsTheSame(
                oldItem: Tailor,
                newItem: Tailor,
            ): Boolean {
                return oldItem.storeName == newItem.storeName
            }

            override fun areContentsTheSame(
                oldItem: Tailor,
                newItem: Tailor,
            ): Boolean {
                return oldItem == newItem
            }
        }

    }
}