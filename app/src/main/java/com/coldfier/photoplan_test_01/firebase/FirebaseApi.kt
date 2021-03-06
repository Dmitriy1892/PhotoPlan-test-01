package com.coldfier.photoplan_test_01.firebase

import android.util.Log
import com.coldfier.photoplan_test_01.model.Folder
import com.coldfier.photoplan_test_01.model.ImageItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FirebaseApi {

    fun addImageToFirebaseStorage(folderId: String, imageItem: ImageItem) {
        val uploadTask = getFirebaseStorage().reference.child("$folderId/${imageItem.imageId}")
        uploadTask.putFile(imageItem.imageUri)
    }

    fun addFolderToCloudFirestore(folder: Folder) {
        val data = mapOf(folder.folderId to folder.folderName)
        getFirestore().collection("locations")
            .document("streets")
            .set(data, SetOptions.merge()).addOnSuccessListener {
                Log.d("fire", "success with cloud firestore adding")
            }.addOnFailureListener {
                Log.d("fire", "fail with cloud firestore adding")
            }
    }

    fun updateFolderCloudFirestoreName(folderId: String, newFolderName: String) {
        getFirestore().collection("locations")
            .document("streets")
            .update(folderId, newFolderName)
            .addOnSuccessListener {
                Log.d("fire", "success with cloud firestore updating")
            }
    }

    fun getFolderListFromCloudFirestore() : List<Folder> {
        val folderList = mutableListOf<Folder>()
        getFirestore().collection("locations").document("streets").get().addOnSuccessListener {
            it.data?.forEach { (key, value) ->
                apply {

                    val imageItemList = getImagesFromFirebaseStorage(key)
                    folderList.add(Folder(key, value.toString(), imageItemList))
                } }
        }
        return folderList
    }

    fun getImagesFromFirebaseStorage(folderName: String): MutableList<ImageItem> {
        val listImageItem = mutableListOf<ImageItem>()
        val storageRef = getFirebaseStorage().reference.child(folderName)
        storageRef.listAll().addOnSuccessListener { listResult ->
            //???????? ?????????? ???????????? ???? ???????? ?? ?????????????????????? ???????????? ???? ????????????????
            listResult.items.forEach { storageReference ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageItem = ImageItem(uri.lastPathSegment.toString(), uri)
                    listImageItem.add(imageItem)
                }
            }
        }
        return listImageItem
    }

    fun deleteImageFromFirebaseStorage(folderName: String, imageItem: ImageItem) {
        val storageRef = getFirebaseStorage().reference.child("${imageItem.imageUri.lastPathSegment}")
        storageRef.delete().addOnSuccessListener {
            Log.d("fire", "success with firebase storage deleting")
        }.addOnFailureListener {
            Log.d("fire", "fail to deleting")
        }
    }

    fun test() {
        val docRef = getFirestore().collection("locations").get().addOnSuccessListener {
            val mainFolderName = it.documents[0].id
        }
    }

    companion object {

        @Volatile
        private var FIRESTORE: FirebaseFirestore? = null
        fun getFirestore(): FirebaseFirestore {
            if (FIRESTORE == null) {
                FIRESTORE = Firebase.firestore
            }
            return FIRESTORE as FirebaseFirestore
        }

        @Volatile
        private var FIREBASE_STORAGE: FirebaseStorage? = null
        fun getFirebaseStorage(): FirebaseStorage {
            if (FIREBASE_STORAGE == null) {
                FIREBASE_STORAGE = Firebase.storage("gs://photoplan-test.appspot.com/")
            }
            return FIREBASE_STORAGE as FirebaseStorage
        }
    }
}