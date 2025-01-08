package com.example.adminpureveg.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminpureveg.databinding.ItemOrderDetailsBinding

class OrderDetailsAdapter(
    private val context: Context,
    private val foodNames: ArrayList<String> = arrayListOf(),
    private val foodImages: ArrayList<String> = arrayListOf(),
    private val foodQuantities: ArrayList<Int> = arrayListOf(),
    private val foodPrices: ArrayList<String> = arrayListOf()
): RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsAdapter.OrderDetailsViewHolder {
        val binding = ItemOrderDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsAdapter.OrderDetailsViewHolder, position: Int) {
        return holder.bind(position)
    }

    override fun getItemCount(): Int = foodNames.size

    inner class OrderDetailsViewHolder(private val binding: ItemOrderDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvOrderDetailsFoodName.text = foodNames[position]
                tvOrderDetailsItemQuantity.text = "${foodQuantities[position]}"
                val price = "Rs: "+foodPrices[position]
                tvOrderDetailsItemPrice.text = price
                val uri = Uri.parse(foodImages[position])
                Glide.with(context).load(uri).into(ivOrderDetailsFoodImage)
            }
        }

    }
}