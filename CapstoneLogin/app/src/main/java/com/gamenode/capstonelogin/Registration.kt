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

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_registration)


        val btn_register = findViewById<Button>(R.id.registerpagebutton)

        btn_register.setOnClickListener() {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(findViewById<ContentFrameLayout>(android.R.id.content).windowToken, 0)

            val contextView = findViewById<View>(R.id.myView)
            var email = findViewById<TextInputEditText>(R.id.etemailregister).text.toString()
            var password = findViewById<TextInputEditText>(R.id.etpasswordregister).text.toString()
            var name = findViewById<TextInputEditText>(R.id.etfullname).text.toString()

            if (name == "") {
                Snackbar.make(contextView, "Error: Name is required", Snackbar.LENGTH_LONG).show()
            } else if (email == "") {
                Snackbar.make(contextView, "Error: Email is required", Snackbar.LENGTH_LONG).show()
            } else if (password == "") {
                Snackbar.make(contextView, "Error: Password is required.", Snackbar.LENGTH_LONG).show()
            } else {
                val delim = " ";
                var nameList = name.split(delim)
                nameList = nameList.plus("")
                var firstname = nameList[0]
                var lastname = nameList[1]
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
                            if (data != null) {
                                Snackbar.make(contextView, data, Snackbar.LENGTH_LONG).show()
                            }
                        }
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