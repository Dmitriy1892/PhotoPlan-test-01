package com.coldfier.photoplan_test_01.locationsfragment

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class LocationsViewModel : ViewModel() {

    private val _imageList = MutableLiveData<MutableList<Bitmap>>()
    val imageList: LiveData<MutableList<Bitmap>>
        get() = _imageList

    var listRefs = mutableListOf<StorageReference>()


    fun addBitmap(bitmap: Bitmap) {
        _imageList.value?.add(bitmap)
    }

    fun addImageToFirebase(bitmap: Bitmap, uri: Uri, folderContentName: String) {
        viewModelScope.launch {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val path = Firebase.storage.reference.child(folderContentName + "/${uri.lastPathSegment}")
            path.putBytes(data)

        }
    }

    fun getImageFromFirebase(folderContentName: String) {
        viewModelScope.launch {

            val storageRef = Firebase.storage("gs://photoplan-test.appspot.com").reference.child("/$folderContentName")
            storageRef.listAll().addOnSuccessListener {
               it.let { it ->
                   listRefs = mutableListOf()
                   //сюда летят ссылки на фото, здесь надо формировать лист с Bitmap или лист со ссылками
                   it.items.forEach{
                           listRefs.add(it)
                   }
                   //не прилетают - никак не обрабатывать
                   it.prefixes.forEach {
                       prefix -> val pref = prefix
                   }
               }
            }
        }
    }
}