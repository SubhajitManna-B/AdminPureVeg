package com.example.adminpureveg.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminpureveg.databinding.ItemPendingOrderBinding

class PendingOrderAdapter(
    private val context: Context,
    private val customerName: MutableList<String>,
    private val foodTotalPrice: MutableList<String>,
    private val foodImage: MutableList<String>,
    private val orderAccepted: MutableList<Boolean>,
    private val itemClicked: OnItemClicked
):
    RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    interface OnItemClicked {
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = ItemPendingOrderBinding.inflate(LayoutInflater.from(parent.context), parent,  false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerName.size

    inner class PendingOrderViewHolder(private val binding: ItemPendingOrderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvPendingCustomerName.text = customerName[position]
                val price = foodTotalPrice[position]
                tvPendingFoodTotalPrice.text = price
                val uri = Uri.parse(foodImage[position])
                Glide.with(context).load(uri).into(ivPendingFoodImage)
                var isAccepted = orderAccepted[position]
                btnPendingOrderAccept.apply {
                    text = if(!isAccepted){
                        "Accept"
                    } else{
                        "Dispatch"
                    }
                    setOnClickListener {
                        if(!isAccepted){
                            text = "Dispatch"
                            isAccepted = true
                            itemClicked.onItemAcceptClickListener(position)
                        }
                        else{
                            customerName.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            itemClicked.onItemDispatchClickListener(position)
                        }
                    }
                }
                itemView.setOnClickListener {
                    itemClicked.onItemClickListener(position)
                }
            }
        }

    }
}