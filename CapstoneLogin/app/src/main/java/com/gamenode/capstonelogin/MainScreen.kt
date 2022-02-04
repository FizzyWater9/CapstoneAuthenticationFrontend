package com.gamenode.capstonelogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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


    }
}