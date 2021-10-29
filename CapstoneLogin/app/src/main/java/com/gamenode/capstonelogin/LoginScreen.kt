package com.gamenode.capstonelogin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.ContentFrameLayout
import com.gamenode.capstonelogin.api.ErrorDisplay
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.reflect.TypeToken
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException


////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
// onCreate function - Page load up and listeners for buttons
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////


const val BASE_URL = "http://gamenode.online"
var RC_SIGN_IN = 0

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // hide the title bar
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<TextInputEditText>(R.id.email)
        val etPassword = findViewById<TextInputEditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.buttonLogin)
        val btnRegister = findViewById<Button>(R.id.buttonRegister)

        val signIn = findViewById<SignInButton>(R.id.sign_in_button)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signIn.setOnClickListener {
            signIn(mGoogleSignInClient)
        }

        btnLogin.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(findViewById<ContentFrameLayout>(android.R.id.content).windowToken, 0)
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiRequests::class.java)
            GlobalScope.launch(Dispatchers.IO) {

                val response = api.getLogin(email, password).awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    //Log.d(TAG, data.email)

                    val firstName = data.firstname
                    val lastName = data.lastname
                    val id = data.id

                    withContext(Dispatchers.Main) {
                        login(firstName, lastName, id)
                    }
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorDisplay>() {}.type
                    val errorResponse: ErrorDisplay? = gson.fromJson(response.errorBody()?.string(), type)
                    val data = errorResponse?.id

                    withContext(Dispatchers.Main) {
                        val contextView = findViewById<View>(R.id.myView)
                        if (data != null) {
                            Snackbar.make(contextView, data, Snackbar.LENGTH_LONG).show()
                        }

                    }
                }
            }

        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
// login function for native login request
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun login(firstname: String, lastname: String, id: String) {
        val intent = Intent(this, MainScreen::class.java)
        intent.putExtra("id", id)
        intent.putExtra("firstname", firstname)
        intent.putExtra("lastname", lastname)
        startActivity(intent)
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
// login functions for google authentication api
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun signIn(mGoogleSignInClient: GoogleSignInClient) {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            val personGivenName = account.givenName
            val personFamilyName = account.familyName
            val personId = account.id
            val intent = Intent(this, MainScreen::class.java)
            intent.putExtra("id", personId)
            intent.putExtra("firstname", personGivenName)
            intent.putExtra("lastname", personFamilyName)
            startActivity(intent)

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

        }

    }
}