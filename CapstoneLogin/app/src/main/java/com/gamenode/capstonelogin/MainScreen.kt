package com.gamenode.capstonelogin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toolbar

class MainScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_main_screen)

        val intent = intent
        val firstname = intent.getStringExtra("firstName")
        val lastname = intent.getStringExtra("lastName")
        val id = intent.getStringExtra("id")
        val toolbar = findViewById<Toolbar>(R.id.myToolBar)

        toolbar.title = "Welcome $firstname $lastname"

        val btnLogout = findViewById<Button>(R.id.logout)

        btnLogout.setOnClickListener() {
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val intent = Intent(this, MainActivity::class.java)
            val empty = ""
            val editor = sharedPreference.edit()
            editor.putString("autologin", "0")
            editor.putString("firstName", empty)
            editor.putString("lastName", empty)
            editor.putString("id", empty)
            editor.apply()

            startActivity(intent)

        }

    }
}