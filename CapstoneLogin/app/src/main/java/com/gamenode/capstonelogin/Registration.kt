package com.gamenode.capstonelogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.gamenode.capstonelogin.api.ErrorDisplay
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.reflect.TypeToken

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        var et_firstname = findViewById<EditText>(R.id.etfirstname)
        var et_lastname = findViewById<EditText>(R.id.etlastname)
        var et_email = findViewById<EditText>(R.id.etemailregister)
        var et_password = findViewById<EditText>(R.id.etpasswordregister)
        var btn_register = findViewById<Button>(R.id.registerpagebutton)

        btn_register.setOnClickListener() {
            var firstname = et_firstname.text.toString()
            var lastname = et_lastname.text.toString()
            var email = et_email.text.toString()
            var password = et_password.text.toString()
            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiRequests::class.java)
            GlobalScope.launch(Dispatchers.IO) {

                val response = api.addUser(email, password, firstname, lastname).awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    //log.d(TAG, data.email)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Account Successfully Created", Toast.LENGTH_SHORT).show()
                    }
                    switchBack()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorDisplay>() {}.type
                    val errorResponse: ErrorDisplay? = gson.fromJson(response.errorBody()?.string(), type)
                    val data = errorResponse?.id

                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, data, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun switchBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}