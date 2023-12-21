package com.capstoneproject.clothizeapp.tailor.data.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.clothizeapp.R
import com.capstoneproject.clothizeapp.client.api.response.Order
import com.capstoneproject.clothizeapp.databinding.ItemOrderTailorBinding
import com.capstoneproject.clothizeapp.tailor.ui.detail.TailorDetailOrderActivity

class TailorOrderAdapter() :
    ListAdapter<Order, TailorOrderAdapter.OrderViewHolder>(DIFF_CALLBACK) {

    private var originalList = mutableListOf<Order>()
    class OrderViewHolder(private var binding: ItemOrderTailorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemCount: Int, position: Int, order: Order) {

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

                tvClient.text = order.clientName
                tvOrderType.text = "${order.service} - ${order.gender}"
                tvQuantity.text = "Qty: ${order.qty} Pcs"

                tvStatus.text = order.status
                tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                when(order.status?.lowercase()){
                    "finished" -> {
                        tvStatus.background = itemView.context.resources.getDrawable(R.drawable.bg_green_finish)
                    }
                    "on-progress" -> {
                        tvStatus.background = itemView.context.resources.getDrawable(R.drawable.bg_yellow_on_progress)
                    }
                    "rejected"-> {
                        tvStatus.background = itemView.context.resources.getDrawable(R.drawable.bg_red_rejected)
                        tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                    }
                    else -> {
                        tvStatus.background = itemView.context.resources.getDrawable(R.drawable.bg_gray_pending)
                    }
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemOrderTailorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }


    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        if (order != null) {
            holder.bind(itemCount, position, order)
            holder.itemView.setOnClickListener {
                val intentToDetailOrder = Intent(holder.itemView.context, TailorDetailOrderActivity::class.java)
                intentToDetailOrder.putExtra(TailorDetailOrderActivity.ORDER_ID, order)
                holder.itemView.context.startActivity(intentToDetailOrder)
            }
        }
    }

    fun searchOrder(query: String): Int {
        val filteredList = originalList.filter { model ->
            model.clientName!!.contains(query, ignoreCase = true)
        }
        submitList(filteredList)

        return filteredList.size
    }

    fun setData(list: MutableList<Order>) {
        originalList = list
        submitList(originalList)
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(
                oldItem: Order,
                newItem: Order,
            ): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(
                oldItem: Order,
                newItem: Order,
            ): Boolean {
                return oldItem == newItem
            }
        }

    }

}