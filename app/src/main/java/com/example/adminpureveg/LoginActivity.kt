package com.example.adminpureveg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminpureveg.databinding.ActivityLoginBinding
import com.example.adminpureveg.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var gsc : GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Google sign-in options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        //initialize firebase auth
        auth = Firebase.auth
        //initialize firebase database
        database = Firebase.database.reference
        //Initialize google sign in client
        gsc = GoogleSignIn.getClient(this, gso)

        //login button
        binding.btnLogin.setOnClickListener{
            //Get text from edit text
            email = binding.etLoginEmail.text.toString().trim()
            password = binding.etLoginPassword.text.toString().trim()

            //Check the filed are empty or not
            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all the details", Toast.LENGTH_SHORT).show()
            } else{
                createAdmin(email, password)
            }
        }

        binding.btnLoginGoogle.setOnClickListener {
            val signInIntent = gsc.signInIntent
            launcher.launch(signInIntent)
        }

        //Don't have account button
        binding.tvbtnSignUp.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Create admin account
    private fun createAdmin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if (task.isSuccessful){
                val user: FirebaseUser? = auth.currentUser
                Toast.makeText(this,"Account Login successfully", Toast.LENGTH_SHORT).show()
                updateUi(user)
            }else{
                Toast.makeText(this,"Authentication Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account","CreateUserAccount : Authentication Failed", task.exception)
            }
        }
    }

    //Launcher for google sign-in
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if(result.resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener{authTask ->
                    if(authTask.isSuccessful){
                        Toast.makeText(this,"Successfully sign-in with Google", Toast.LENGTH_SHORT).show()
                        updateUi(authTask.result?.user)
                    }else{
                        Toast.makeText(this,"Google sign-in failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Redirect the page from login to main activity
    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    //Check if user is already log in
    override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}