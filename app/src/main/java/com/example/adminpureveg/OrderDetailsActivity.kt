package com.example.adminpureveg

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminpureveg.adapter.OrderDetailsAdapter
import com.example.adminpureveg.adapter.PendingOrderAdapter
import com.example.adminpureveg.databinding.ActivityOrderDetailsBinding
import com.example.adminpureveg.model.OrderDetailsModel
import com.google.firebase.database.FirebaseDatabase

class OrderDetailsActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }
    private var userName: String? = null
    private var userAddress: String? = null
    private var userPhoneNumber: String? = null
    private var totalPrice: String? = null
    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodQuantities: ArrayList<Int> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnOrderDetailsBack.setOnClickListener {
            finish()
        }

        //get data from the pending order activity intent
        getDataFromIntent()
    }

    private fun getDataFromIntent() {
        val receivedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as OrderDetailsModel
        receivedOrderDetails.let{orderDetails ->
            userName = orderDetails.userName
            userAddress = orderDetails.address
            userPhoneNumber = orderDetails.phoneNo
            totalPrice = orderDetails.totalPrice
            foodNames = orderDetails.foodNames as ArrayList<String>
            foodImages = orderDetails.foodImages as ArrayList<String>
            foodQuantities = orderDetails.foodQuantities as ArrayList<Int>
            foodPrices = orderDetails.foodPrices as ArrayList<String>

            //Show user details
            setUserDetails()
            //set order menu item data on adapter
            setAdapter()
        }
    }

    private fun setUserDetails() {
        binding.apply {
            tvOrderDtlUserName.text = userName
            tvOrderDtlUserAddress.text = userAddress
            tvOrderDtlUserPhone.text = userPhoneNumber
            val price = "Rs: $totalPrice"
            tvOrderDtlTotalAmount.text = price
        }
    }

    private fun setAdapter() {
        val adapter = OrderDetailsAdapter(this, foodNames, foodImages, foodQuantities, foodPrices)
        binding.rvOrderDetails.layoutManager = LinearLayoutManager(this)
        binding.rvOrderDetails.adapter = adapter
    }
}