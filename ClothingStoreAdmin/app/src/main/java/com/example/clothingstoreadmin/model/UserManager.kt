package com.example.clothingstoreadmin.model

class UserManager private constructor() {
    private var user: Customer? = null

    companion object {
        @Volatile
        private var instance: UserManager? = null

        fun getInstance(): UserManager {
            return instance ?: synchronized(this) {
                instance ?: UserManager().also { instance = it }
            }
        }
    }

    fun setUser(cus:Customer) {
        user = cus
    }

    fun getUserCurrent():Customer?{
        return user
    }

    fun getUserID(): String? {
        return user?.id
    }
}
