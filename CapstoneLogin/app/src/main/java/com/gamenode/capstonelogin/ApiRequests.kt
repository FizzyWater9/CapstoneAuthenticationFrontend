package com.gamenode.capstonelogin

import com.gamenode.capstonelogin.api.loginjson
import com.gamenode.capstonelogin.api.userCreated
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiRequests {

    @GET("/login")
    fun getLogin(@Query("email") email: String, @Query("password") password: String ): Call<loginjson>

    @POST("/adduser")
    fun addUser(@Query("email") email: String, @Query("password") password: String, @Query("firstname") firstname: String, @Query("lastname") lastname: String): Call<userCreated>
}