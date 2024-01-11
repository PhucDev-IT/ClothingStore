package com.example.clothingstoreapp.Service

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {
    private var firebaseAppInstance: FirebaseApp? = null
    @SuppressLint("StaticFieldLeak")
    private var firestoreInstance: FirebaseFirestore? = null

    fun initializeFirebaseApp(context: Context) {
        if (firebaseAppInstance == null) {
            firebaseAppInstance = FirebaseApp.initializeApp(context)
        }
    }

    fun getFirestoreInstance(): FirebaseFirestore {
        if (firestoreInstance == null) {
            firestoreInstance = FirebaseFirestore.getInstance(firebaseAppInstance!!)
        }
        return firestoreInstance!!
    }
}
