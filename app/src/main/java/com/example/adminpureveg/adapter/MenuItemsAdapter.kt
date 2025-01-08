package com.example.adminpureveg.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminpureveg.databinding.ItemAllItemsBinding
import com.example.adminpureveg.model.AllMenuModel

class MenuItemsAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenuModel>,
    private val onDeleteClickListener: (position: Int) -> Unit
): RecyclerView.Adapter<MenuItemsAdapter.AllItemsViewHolder>() {
    private val itemQuantity = IntArray(menuList.size){1}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllItemsViewHolder {
        val binding = ItemAllItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllItemsViewHolder, position: Int) {
        anim(holder.itemView)
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AllItemsViewHolder(private val binding: ItemAllItemsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val menuItem = menuList[position]
                val uriString: String? = menuItem.foodImage
                val uri: Uri? = Uri.parse(uriString)
                tvAllItemsFoodName.text = menuItem.foodName
                tvAllItemsFoodPrice.text = menuItem.foodPrice
                Glide.with(context).load(uri).into(ivAllItemsImage)
                binding.tvAllItemsFoodCount.text = "${itemQuantity[position]}"

                ibtnAllItemsAddFood.setOnClickListener {
                    increaseQuantity(position)
                }
                ibtnAllItemsRemoveFood.setOnClickListener {
                    decreaseQuantity(position)
                }
                ibtnAllItemsDeleteFood.setOnClickListener {
                    onDeleteClickListener(position)
                }

            }
        }

        private fun increaseQuantity(position: Int) {
            if(itemQuantity[position] < 15){
                itemQuantity[position]++;
                binding.tvAllItemsFoodCount.text = "${itemQuantity[position]}"
            }
        }

        private fun decreaseQuantity(position: Int) {
            if(itemQuantity[position] > 1){
                itemQuantity[position]--
                binding.tvAllItemsFoodCount.text = "${itemQuantity[position]}"
            }
        }
    }

    //Animation in recycler view
    private fun anim(view: View){
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 1000
        view.startAnimation(animation)
    }
}