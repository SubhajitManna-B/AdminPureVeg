package com.example.adminpureveg

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminpureveg.databinding.ActivityAddItemBinding
import com.example.adminpureveg.model.AllMenuModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {
    private val binding : ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }
    private lateinit var foodName : String
    private lateinit var foodPrice : String
    private var foodImage : Uri? = null
    private lateinit var foodDescription : String
    private lateinit var foodIngredients : String
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //initialize firebase auth
        auth = Firebase.auth
        //initialize firebase database instance
        database = Firebase.database.reference

        binding.btnAddItem.setOnClickListener {
            //Get data from fields(edit text)
            foodName = binding.etAddItemName.text.toString().trim()
            foodPrice = binding.etAddItemPrice.text.toString().trim()
            foodDescription = binding.etAddItemDescription. text.toString().trim()
            foodIngredients = binding.etAddItemIngredient.text.toString().trim()

            if (foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() || foodIngredients.isBlank()){
                Toast.makeText(this,"Please fill all the details", Toast.LENGTH_SHORT).show()
            }else{
                uploadData()
                Toast.makeText(this,"Item is Added Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnTvAddItemImage.setOnClickListener{
            pickImage.launch("image/*")
        }

        binding.btnIvAddItemBack.setOnClickListener{
            finish()
        }
    }

    private fun uploadData() {
        //Get current user
        val userId = auth.currentUser?.uid?:""
        //Get a reference to the database
        val menuRef = database.ref.child("admin").child("menu")
        //Generate a unique key for the new menu item
        val newItemKey:String? = menuRef.push().key

        if(foodImage != null){
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImage!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    //Create a new menu item object
                    val newItem = AllMenuModel(newItemKey, foodName, foodPrice, downloadUrl.toString(), foodDescription, foodIngredients)
                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this,"Data Uploaded Successfully", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this,"Data Upload Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
                .addOnFailureListener {
                    Toast.makeText(this,"Image Upload Failed", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this,"Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    //Pick image from device
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.ivAddItemImage.setImageURI(uri)
            foodImage = uri
        }
    }
}