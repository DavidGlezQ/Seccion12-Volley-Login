package com.david_glez.seccion12_volley_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.david_glez.seccion12_volley_login.databinding.ActivityMainBinding
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.swType.setOnCheckedChangeListener { button, checked ->
            button.text = if (checked) "Login" else "Register"

            mBinding.btnLogin.text = button.text
        }

        mBinding.btnUser.setOnClickListener { loadUser() }

        mBinding.btnLogin.setOnClickListener { login() }
    }

    private fun loadUser() {
        val url = Constans.BASE_URL + Constans.API_PATH + Constans.API_USER
        val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null,{ response ->
            val gson = Gson()

            val userJson = response.optJSONObject(Constans.DATA_PROPERTY)?.toString()
            val user: User = gson.fromJson(userJson, User::class.java)

            updateUser(user)
        },{
            it.printStackTrace()
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                return params
            }
        }
        LoginApplication.reqResAPI.addToRequestQueue(jsonObjectRequest)
    }

    private fun updateUser(user: User) {
        with(mBinding){
            Glide.with(this@MainActivity)
                .load(user.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .circleCrop()
                .into(ivAvatar)

            tvName.text = user.getCompleteName()
            tvEmail.text = user.email
        }
    }

    private fun login() {
        val typeMethod = if(mBinding.swType.isChecked) Constans.LOGIN_PATH
        else Constans.REGISTER_PATH

        val url = Constans.BASE_URL + Constans.API_PATH + typeMethod
        val email = mBinding.etMail.text.toString().trim()
        val password = mBinding.etPassword.text.toString().trim()

        val jsonParams = JSONObject()

        if (email.isNotEmpty()){ jsonParams.put(Constans.EMAIL_PARAM, email) }
        if (password.isNotEmpty()){ jsonParams.put(Constans.PASSWORD_PARAM, password) }

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams, { response ->
            Log.i("response", response.toString())

            val id = response.optString(Constans.ID_PROPERTY, Constans.ERROR_VALUE)
            val token = response.optString(Constans.TOKEN_PROPERTY, Constans.ERROR_VALUE)

            val result = if (id.equals(Constans.ERROR_VALUE)) "${Constans.TOKEN_PROPERTY}: $token"
            else "${Constans.ID_PROPERTY}: $id, ${Constans.TOKEN_PROPERTY}: $token"
            updateUi(result)
        },{
            it.printStackTrace()
            if (it.networkResponse.statusCode == 400){
                updateUi(getString(R.string.main_error_server))
            }
        }){
            //body counters y headers
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                return params
            }
        }
        LoginApplication.reqResAPI.addToRequestQueue(jsonObjectRequest)
    }

    private fun updateUi(result: String) {
        mBinding.tvResult.visibility = View.VISIBLE
        mBinding.tvResult.text = result

    }
}