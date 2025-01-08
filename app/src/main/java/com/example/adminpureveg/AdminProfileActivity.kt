package com.example.adminpureveg

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminpureveg.databinding.ActivityAdminProfileBinding
import com.example.adminpureveg.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var currentAdmin: String
    private lateinit var database: FirebaseDatabase
    private lateinit var profileDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Initialize auth and currentAdmin
        auth = FirebaseAuth.getInstance()
        currentAdmin = auth.currentUser?.uid?:""
        //Initialize database and database reference
        database = FirebaseDatabase.getInstance()
        profileDatabaseReference = database.reference.child("admin").child("admin_profile")

        //set back button
        binding.btnIvAdminProfileBack.setOnClickListener {
            finish()
        }

        //Set all the edit text by default disable for edit
        binding.etProfileName.isEnabled = false
        binding.etProfileAdderess.isEnabled = false
        binding.etProfileMail.isEnabled = false
        binding.etProfilePhone.isEnabled = false
        binding.etProfilePassword.isEnabled = false
        binding.btnProfileDataSave.isEnabled = false
        var isEnable = false
        //On click of button edit text can be enable or disable for edit
        binding.tvEditProfile.setOnClickListener {
            isEnable = !isEnable
            binding.etProfileName.isEnabled = isEnable
            binding.etProfileAdderess.isEnabled = isEnable
            binding.etProfileMail.isEnabled = isEnable
            binding.etProfilePhone.isEnabled = isEnable
            binding.etProfilePassword.isEnabled = isEnable
            binding.btnProfileDataSave.isEnabled = isEnable
            if (isEnable) {
                binding.etProfileName.requestFocus()
            }
        }

        //Set the data on profile edit text
        setEditTextData()

        //Set the function of save information button
        binding.btnProfileDataSave.setOnClickListener {
            val name = binding.etProfileName.text.toString().trim()
            val address = binding.etProfileAdderess.text.toString().trim()
            val email = binding.etProfileMail.text.toString().trim()
            val phone = binding.etProfilePhone.text.toString().trim()
            val password = binding.etProfilePassword.text.toString().trim()
            //Update admin profile data in database
            updateProfileDatabase(name,address,email,phone,password)
        }
    }

    //Function to set the data on admin profile
    private fun setEditTextData() {
        profileDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val profile = snapshot.getValue(UserModel::class.java)
                    if(profile != null) {
                        binding.apply {
                            etProfileName.setText(profile.adminName)
                            etProfileAdderess.setText(profile.address)
                            etProfileMail.setText(profile.email)
                            etProfilePhone.setText(profile.phone)
                            etProfilePassword.setText(profile.password)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }
        })
    }

    //Function to Update admin profile data in database
    private fun updateProfileDatabase(name: String, address: String, email: String, phone: String, password: String) {
        val userModel = UserModel(name,"", email, password, address, phone)
        profileDatabaseReference.setValue(userModel).addOnSuccessListener {
            Toast.makeText(this,"Information was updated successfully😊", Toast.LENGTH_SHORT).show()
            //Update the email and password for firebase authentication
            auth.currentUser?.updateEmail(email)
            auth.currentUser?.updatePassword(password)
        }.addOnFailureListener {
            Toast.makeText(this,"Information was not updated successfully😢",Toast.LENGTH_SHORT).show()
        }
    }
}