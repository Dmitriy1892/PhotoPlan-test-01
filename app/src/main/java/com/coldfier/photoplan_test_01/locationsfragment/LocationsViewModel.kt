package com.coldfier.photoplan_test_01.locationsfragment

import android.app.Application
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.coldfier.photoplan_test_01.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayOutputStream
import java.net.URL

class LocationsViewModel(application: Application) : AndroidViewModel(application) {

    val data = MutableLiveData<MutableMap<String, Int>>()

    val appCtx = application

    private val _imagesList = MutableLiveData<List<Bitmap>>()
    val imagesList : LiveData<List<Bitmap>>
        get() = _imagesList

    @Volatile
    var listRefs = mutableListOf<Uri>()

    @Volatile
    var bufferImagesList = mutableListOf<Bitmap>()

    init {
        _imagesList.value = mutableListOf()
    }

    fun addImage(bitmap: Bitmap) {
        viewModelScope.launch {
            val buff = _imagesList.value?.toMutableList()
            buff?.add(bitmap)
            _imagesList.value = buff!!
        }
    }

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
                    //сюда летят ссылки на фото и формируется список со ссылками
                    it.items.forEach{
                        it.downloadUrl.addOnSuccessListener {
                            listRefs.add(it)
                        }
                    }
                }
            }
        }
    }
}