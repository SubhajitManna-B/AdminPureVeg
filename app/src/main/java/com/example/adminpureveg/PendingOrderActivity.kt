package com.example.adminpureveg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminpureveg.adapter.PendingOrderAdapter
import com.example.adminpureveg.databinding.ActivityPendingOrderBinding
import com.example.adminpureveg.model.OrderDetailsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {
    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfUserName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItems: MutableList<OrderDetailsModel> = mutableListOf()
    private var listOfOrderAccepted: MutableList<Boolean> = mutableListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialization of database and database reference
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference.child("order_details")

        //retrieve all the order details
        getOrderDetails()

        //set back button
        binding.btnIvPendingOrderBack.setOnClickListener {
            finish()
        }

    }

    //Function to get all the order details
    private fun getOrderDetails() {
        databaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children){
                    val orderItem = orderSnapshot.getValue(OrderDetailsModel::class.java)
                    orderItem?.let {
                        listOfOrderItems.add(it)
                    }
                }
                //Add data to the list
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }

        })
    }

    //Function to add data to the respected list
    private fun addDataToListForRecyclerView() {
        for(orderItem in listOfOrderItems){
            orderItem.userName?.let { listOfUserName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot{it.isEmpty()}?.forEach { listOfImageFirstFoodOrder.add(it) }
            orderItem.orderAccepted.let { listOfOrderAccepted.add(it) }
        }
        //Set the adapter for recycler view
        setAdapter()
    }

    //Function to set adapter for recycler view
    private fun setAdapter() {
        val adapter = PendingOrderAdapter(this, listOfUserName, listOfTotalPrice, listOfImageFirstFoodOrder,listOfOrderAccepted ,this)
        binding.rvPendingOrderList.layoutManager = LinearLayoutManager(this)
        binding.rvPendingOrderList.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItems[position]
        intent.putExtra("UserOrderDetails",userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        //Handle order acceptance and update database
        val childItemPushKey = listOfOrderItems[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            databaseRef.child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
            ?.addOnSuccessListener {
                Toast.makeText(this, "Order is accepted", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                Toast.makeText(this, "Order is not accepted", Toast.LENGTH_SHORT).show()
            }
        //Update user buy history database
        updateOrderAcceptStatus(position)
    }

    override fun onItemDispatchClickListener(position: Int) {
        val dispatchItemPushKey = listOfOrderItems[position].itemPushKey
        val dispatchOrderRef = database.reference.child("completed_Order").child(dispatchItemPushKey!!)
        dispatchOrderRef.setValue(listOfOrderItems[position])
            .addOnSuccessListener {
                deleteItemFromOrderDetailsDatabase(dispatchItemPushKey)
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        val userIdOfClickedItem = listOfOrderItems[position].userId
        val itemPushKey = listOfOrderItems[position].itemPushKey
        val orderHistoryRef = database.reference.child("user").child(userIdOfClickedItem!!).child("buy_history").child(itemPushKey!!)
        orderHistoryRef.child("orderAccepted").setValue(true)
    }

    private fun deleteItemFromOrderDetailsDatabase(itemPushKey: String) {
        val orderDetailsItemRef = databaseRef.child(itemPushKey)
        orderDetailsItemRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this,"Order is dispatched",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Order is not dispatched",Toast.LENGTH_SHORT).show()
            }
    }
}