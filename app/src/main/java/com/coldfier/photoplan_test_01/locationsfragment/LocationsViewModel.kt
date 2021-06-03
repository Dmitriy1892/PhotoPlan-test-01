package com.coldfier.photoplan_test_01.locationsfragment

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LocationsViewModel : ViewModel() {

    val data = MutableLiveData<MutableMap<String, Int>>()

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

    fun getClick() {
        viewModelScope.launch {
            val db = Firebase.firestore

            val map = db.collection("users")
                .get()
                .addOnSuccessListener { snapshot ->
                    run {
                        Log.d("fire", "success")
                        val mappp = mutableMapOf<String, Int>()
                        snapshot.documents[0]
                            .data?.forEach {
                                    (key, value) ->
                                mappp[key] = value.toString().toInt()
                            }
                        data.value = mappp
                    }

                }.addOnFailureListener{
                    Log.d("fire", "fail")
                }
        }


    }
}