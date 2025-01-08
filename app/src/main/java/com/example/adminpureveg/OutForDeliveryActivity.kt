package com.example.adminpureveg

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminpureveg.adapter.DeliveryAdapter
import com.example.adminpureveg.databinding.ActivityOutForDeliveryBinding
import com.example.adminpureveg.model.OrderDetailsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy{
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private var completeOrderList: ArrayList<OrderDetailsModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Initialize database reference
        database = FirebaseDatabase.getInstance()

        //Set the function of back button
        binding.btnIvDeliveryBack.setOnClickListener {
            finish()
        }

        //Retrieve the data from database
        retrieveData()
    }

    //Function to retrieve data from database
    private fun retrieveData() {
        val completeOrderRef = database.reference.child("completed_Order").orderByChild("currentTime")
        completeOrderRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Clear the previous list and add new data
                completeOrderList.clear()
                for(orderSnapshot in snapshot.children){
                    val completeItem = orderSnapshot.getValue(OrderDetailsModel::class.java)
                    completeItem?.let {
                        completeOrderList.add(it)
                    }
                }
                //Reverse the list to display latest order first
                completeOrderList.reverse()
                //Set data on adapter
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }

        })
    }

    //Function to set data on adapter
    private fun setAdapter() {
        //Initialize user name name and payment status
        val userName = arrayListOf<String>()
        val paymentStatus = arrayListOf<Boolean>()
        for (order in completeOrderList){
            order.userName?.let { userName.add(it) }
            paymentStatus.add(order.paymentReceived)
        }
        val adapter = DeliveryAdapter(userName, paymentStatus)
        binding.rvDeliveryList.layoutManager = LinearLayoutManager(this)
        binding.rvDeliveryList.adapter = adapter
    }
}