package com.david_glez.seccion12_volley_login

import android.app.Application

class LoginApplication : Application() {

    //Singleton
    companion object{
        lateinit var reqResAPI: ReqResAPI
    }

    override fun onCreate() {
        super.onCreate()

        //Volley
        reqResAPI = ReqResAPI.getInstance(this)
    }
}