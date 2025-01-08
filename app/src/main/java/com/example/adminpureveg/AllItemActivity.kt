package com.example.adminpureveg

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminpureveg.adapter.MenuItemsAdapter
import com.example.adminpureveg.databinding.ActivityAllItemBinding
import com.example.adminpureveg.model.AllMenuModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllItemActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var menuItems: ArrayList<AllMenuModel> = ArrayList()
    private lateinit var adapter : MenuItemsAdapter

    private val binding:ActivityAllItemBinding  by lazy { ActivityAllItemBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setAdapter()

        //initialize firebase auth
        auth = Firebase.auth
        //Initialize database reference
        database = FirebaseDatabase.getInstance()
        retrieveMenuItem()

        //back button
        binding.btnIvAllItemBack.setOnClickListener {
            finish()
        }
    }

    private fun retrieveMenuItem() {
        //Get current user
        val userId = auth.currentUser?.uid?:""
        val foodRef: DatabaseReference = database.reference.child("admin").child("menu")

        //Fetch data from database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Clear existing data before adding new data
                menuItems.clear()

                //Iterate through the data and add it to the menuItems list
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(AllMenuModel::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }
        })
    }

    private fun setAdapter() {
        adapter = MenuItemsAdapter(this, menuItems){position ->
            //Delete item from the menu based on current position
            deleteMenuItem(position)
        }
        binding.rvAllItemsMenu.layoutManager = LinearLayoutManager(this)
        binding.rvAllItemsMenu.adapter = adapter
    }

    //Delete menu item from database and adapter
    private fun deleteMenuItem(position: Int) {
        val menuItemToDelete = menuItems[position]
        val itemKey = menuItemToDelete.itemKey
        val menuItemRef = database.reference.child("admin").child("menu").child(itemKey!!)
        menuItemRef.removeValue().addOnCompleteListener { task ->
            if(task.isSuccessful){
                menuItems.removeAt(position)
                binding.rvAllItemsMenu.adapter?.notifyItemRemoved(position)
                Toast.makeText(this,"Item was deleted Successfully🙂",Toast.LENGTH_SHORT).show()
            }else{
               Toast.makeText(this,"Item was not deleted Successfully😢",Toast.LENGTH_SHORT).show()
            }
        }
    }
}