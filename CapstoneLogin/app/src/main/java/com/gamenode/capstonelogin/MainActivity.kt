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



const val BASE_URL = "http://gamenode.online"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var et_email = findViewById<EditText>(R.id.emailAddress)
        var et_password = findViewById<EditText>(R.id.password)
        var btn_login = findViewById<Button>(R.id.buttonLogin)
        var btn_register = findViewById<Button>(R.id.buttonRegister)

        var TAG = "MainActivity"

        btn_login.setOnClickListener() {
            var email = et_email.text.toString()
            var password = et_password.text.toString()
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

                    var first_name = data.firstname.toString()
                    var last_name = data.lastname.toString()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext,
                            "Succesful login as $first_name $last_name", Toast.LENGTH_SHORT).show()
                    }
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

        btn_register.setOnClickListener() {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
    }
}