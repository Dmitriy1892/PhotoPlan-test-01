package com.coldfier.photoplan_test_01.locationsfragment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LocationsViewModel : ViewModel() {
    fun testClick() {
        val db = Firebase.firestore

        val userMap = hashMapOf("one" to 1, "two" to 2, "three" to 3)

        db.collection("users").add(userMap)
            .addOnSuccessListener {
                Log.d("fire", "success")
            }.addOnFailureListener{
                Log.d("fire", "fail")
            }
    }
}