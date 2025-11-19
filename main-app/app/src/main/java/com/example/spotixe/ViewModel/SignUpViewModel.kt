package com.example.spotixe.viewmodel

/**
 * Singleton object để lưu trữ email và name toàn cục
 * không bị mất khi navigate giữa các screen
 */
object SignUpViewModel {
    var email: String = ""
    var name: String = ""

    fun setData(email: String, name: String) {
        this.email = email
        this.name = name
    }

    fun loadEmail(): String = email
    fun loadName(): String = name

    fun clearData() {
        email = ""
        name = ""
    }
}

