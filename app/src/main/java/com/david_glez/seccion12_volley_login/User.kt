package com.david_glez.seccion12_volley_login

data class User(
    val id: Long,
    val email: String,
    val first_name: String,
    val last_name: String,
    val avatar: String) {

    fun getCompleteName(): String = "$first_name $last_name"
}