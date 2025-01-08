package com.example.adminpureveg.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminpureveg.databinding.ItemOutForDeliveryBinding

class DeliveryAdapter(
    private val customerNames: ArrayList<String>,
    private val paymentStatus: ArrayList<Boolean>
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemOutForDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class DeliveryViewHolder(private val binding: ItemOutForDeliveryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvDeliverCustomerName.text = customerNames[position]
                if (paymentStatus[position]) tvDeliveryPaymentStatus.text = "Received"
                else tvDeliveryPaymentStatus.text = "Not Received"

                val colorMap = mapOf(
                    true to Color.GREEN, false to Color.RED
                )
                tvDeliveryPaymentStatus.setTextColor(colorMap[paymentStatus[position]]?:Color.BLACK)
                cvDeliveryStatusColor.backgroundTintList = ColorStateList.valueOf(colorMap[paymentStatus[position]]?:Color.BLACK)
            }
        }

    }
}
