package com.coldfier.photoplan_test_01.locationsfragment

import android.app.Application
import android.graphics.*
import android.net.Uri
import androidx.lifecycle.*
import com.coldfier.photoplan_test_01.addItem
import com.coldfier.photoplan_test_01.deleteItem
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch

class LocationsViewModel(application: Application) : AndroidViewModel(application) {

    val data = MutableLiveData<MutableMap<String, Int>>()

    val appCtx = application

    @Volatile
    private var _listRefs = MutableLiveData<List<Uri>>()
    val listRefs: LiveData<List<Uri>>
        get() = _listRefs

    init {
        _listRefs.value = listOf()
    }

    fun addUri(uri: Uri) {
        viewModelScope.launch {

            //защита от добавления повторных изображений - сравнение локальных Uri
            listRefs.value?.forEach { if (it == uri) return@launch }
            _listRefs.addItem(uri)
        }
    }

    fun addImageToFirebase(uri: Uri, folderContentName: String) {
        viewModelScope.launch {
            val uploadTask = Firebase.storage.reference.child("$folderContentName/${uri.lastPathSegment}")
            uploadTask.putFile(uri)
        }
    }

    fun getImageFromFirebase(folderContentName: String) {
        viewModelScope.launch {
            val storageRef = Firebase.storage("gs://photoplan-test.appspot.com/").reference.child(folderContentName)
            storageRef.listAll().addOnSuccessListener {
                it.let { it ->
                    _listRefs.value = listOf()
                    //сюда летят ссылки на фото и формируется список со ссылками
                    it.items.forEach{ storageReference ->
                        storageReference.downloadUrl.addOnSuccessListener {
                            _listRefs.addItem(it)
                        }
                    }
                }
            }
        }
    }

    fun deleteImageFromFirebase(uri: Uri, folderContentName: String) {
        viewModelScope.launch {
            val storageRef = Firebase.storage("gs://photoplan-test.appspot.com/").reference.child("$folderContentName/${uri.lastPathSegment}")
            storageRef.delete().addOnSuccessListener {
                _listRefs.deleteItem(uri)
            }
        }
    }
}