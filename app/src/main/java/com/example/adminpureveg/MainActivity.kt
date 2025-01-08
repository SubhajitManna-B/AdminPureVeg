package com.example.adminpureveg

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.adminpureveg.databinding.ActivityMainBinding
import com.example.adminpureveg.model.OrderDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Initialized auth and database reference
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        binding.cvAddNewItem.setOnClickListener{
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        binding.cvShowAllItem.setOnClickListener{
            val intent = Intent(this, AllItemActivity::class.java)
            startActivity(intent)
        }

        binding.cvProfile.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }

        binding.cvCreateNewUser.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }

        binding.cvOrderDispatch.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }

        binding.cvPendingOrder.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }

        binding.cvLogOut.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        //Count the number of pending orders
        countPendingOrder()
        //Count the number of completed orders
        countCompletedOrder()
        //Calculate total earning/whole time earning
        calculateWholeTimeEarning()
    }

    //Function to count the pending orders
    private fun countPendingOrder() {
        var numOfPendingOrder = 0;
        val pendingOrderDetailsRef = databaseRef.child("order_details")
        pendingOrderDetailsRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                numOfPendingOrder = snapshot.childrenCount.toInt()
                binding.tvPendingOrderCount.text = "$numOfPendingOrder"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    //Function to count the completed orders
    private fun countCompletedOrder() {
        var numOfCompletedOrder = 0;
        val completeOrderRef = databaseRef.child("completed_Order")
        completeOrderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                numOfCompletedOrder = snapshot.childrenCount.toInt()
                binding.tvCompletedOrderCount.text = "$numOfCompletedOrder"
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //Function to Calculate total earning/whole time earning
    private fun calculateWholeTimeEarning() {
        val listOfTotalPrices = mutableListOf<Int>()
        val completeOrderRef = databaseRef.child("completed_Order")
        completeOrderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapshot in snapshot.children){
                    val item = itemSnapshot.getValue(OrderDetailsModel::class.java)
                    item?.totalPrice?.toIntOrNull()?.let {
                        listOfTotalPrices.add(it)
                    }
                }
                var earning = listOfTotalPrices.sum().toString()
                binding.tvTotalEarning.text = earning
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}