package com.gamenode.capstonelogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import com.gamenode.capstonelogin.api.ErrorDisplay
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory


class ForgetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_forget_password)

        val email = findViewById<TextInputEditText>(R.id.email)
        val confirmEmail = findViewById<TextInputEditText>(R.id.confirmEmail)
        val btnResetPassword = findViewById<Button>(R.id.resetPassword)

        btnResetPassword.setOnClickListener() {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(findViewById<ContentFrameLayout>(android.R.id.content).windowToken, 0)
            val textEmail = email.text.toString()
            val textConfirmEmail = confirmEmail.text.toString()
            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiRequests::class.java)

            if (textEmail != textConfirmEmail) {
                Toast.makeText(applicationContext, "Email does not match", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val response = api.forgotPassword(textEmail).awaitResponse()
                    if (response.isSuccessful) {
                        val data = response.body()!!
                        val id = data.id

                        withContext(Dispatchers.Main) {
                            returnToMainPage(id)
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
        }
    }

    private fun returnToMainPage(id : String) {
        Toast.makeText(applicationContext, "Reset Password Link Sent!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}