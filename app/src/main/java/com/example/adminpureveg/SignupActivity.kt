package com.example.adminpureveg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.adminpureveg.databinding.ActivitySignupBinding
import com.example.adminpureveg.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignupActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignupBinding
    private lateinit var adminName: String
    private lateinit var nameOfRestaurant: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth : FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize firebase auth
        auth = Firebase.auth
        //initialize firebase database
        database = Firebase.database.reference

        //Create admin account and add on database
        binding.btnSignUp.setOnClickListener{
            //get text from edit text
            adminName = binding.etSignOwnerName.text.toString().trim()
            nameOfRestaurant = binding.etSignRestaurantName.text.toString().trim()
            email = binding.etSignupEmail.text.toString().trim()
            password = binding.etSignupPassword.text.toString().trim()
            if(adminName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all the details", Toast.LENGTH_SHORT).show()
            } else{
                createAccount(email, password)
            }
        }

        //Already have account button
        binding.tvbtnLogIn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val locationList = arrayOf("Kolkata","Odisha","Mumbai","Bangalore","Chennai")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        binding.listOfLocation.setAdapter(adapter)
    }

    //Create admin account
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Account created successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else{
                Toast.makeText(this,"Account creation failed", Toast.LENGTH_SHORT).show()
                Log.d("Account","Create Account: Failure", task.exception)
            }
        }
    }

    //Save all the data in database
    private fun saveUserData() {
        //get text from edit text
        adminName = binding.etSignOwnerName.text.toString().trim()
        nameOfRestaurant = binding.etSignRestaurantName.text.toString().trim()
        email = binding.etSignupEmail.text.toString().trim()
        password = binding.etSignupPassword.text.toString().trim()
        val userModel = UserModel(adminName, nameOfRestaurant, email, password, "", "")
        database.child("admin").child("admin_profile").setValue(userModel) //Save data in firebase database
    }
}