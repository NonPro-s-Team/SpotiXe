package com.example.spotixe.viewmodel

import android.content.Context
import android.content.SharedPreferences

object SignUpViewModel {
    private var context: Context? = null
    private var sharedPref: SharedPreferences? = null

    var email: String = ""
    var name: String = ""

    fun init(context: Context) {
        this.context = context
        this.sharedPref = context.getSharedPreferences("signup_pref", Context.MODE_PRIVATE)
        // Load từ SharedPreferences khi init
        email = sharedPref?.getString("email", "") ?: ""
        name = sharedPref?.getString("name", "") ?: ""
    }

    fun setData(email: String, name: String) {
        this.email = email
        this.name = name
        // Lưu vào SharedPreferences
        sharedPref?.edit()?.apply {
            putString("email", email)
            putString("name", name)
            apply()
        }
    }

    fun loadEmail(): String {
        if (email.isEmpty()) {
            email = sharedPref?.getString("email", "") ?: ""
        }
        return email
    }

    fun loadName(): String {
        if (name.isEmpty()) {
            name = sharedPref?.getString("name", "") ?: ""
        }
        return name
    }

    fun loadUsername(): String {
        if (name.isEmpty()) {
            name = sharedPref?.getString("name", "") ?: ""
        }
        return name
    }

    fun saveName(name: String) {
        this.name = name
        sharedPref?.edit()?.apply {
            putString("name", name)
            apply()
        }
    }

    fun saveEmail(email: String) {
        this.email = email
        sharedPref?.edit()?.apply {
            putString("email", email)
            apply()
        }
    }

    fun clearData() {
        email = ""
        name = ""
        sharedPref?.edit()?.apply {
            remove("email")
            remove("name")
            apply()
        }
    }
}
