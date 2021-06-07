package com.coldfier.photoplan_test_01.locationsfragment

import android.app.Application
import android.graphics.*
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.coldfier.photoplan_test_01.addItem
import com.coldfier.photoplan_test_01.firebase.FirebaseApi
import com.coldfier.photoplan_test_01.model.Folder
import com.coldfier.photoplan_test_01.model.ImageAddContract
import com.coldfier.photoplan_test_01.model.ImageItem
import com.coldfier.photoplan_test_01.updateList
import kotlinx.coroutines.launch

class LocationsViewModel(application: Application) : AndroidViewModel(application) {

    //val appCtx = application

    private var _foldersList = MutableLiveData<List<Folder>>()
    val foldersList: LiveData<List<Folder>>
        get() = _foldersList

    var folderId: String? = null
    var folderName: String? = null

    init {
        _foldersList.value = listOf()
    }

    private val firebaseApi = FirebaseApi()

    fun addImage(imageAddContract: ImageAddContract) {
        viewModelScope.launch {
            _foldersList.value?.forEach{ folder ->
                if (folder.folderId == imageAddContract.folderId) {

                    //если изображение есть в папке, то return
                    folder.imageList.forEach { imageItem ->
                        if (imageItem.imageId == imageAddContract.requestOrUri.toUri().lastPathSegment.toString()) return@launch
                    }

                    val imageItem = ImageItem(
                        imageAddContract.requestOrUri.toUri().lastPathSegment.toString(),
                        imageAddContract.requestOrUri.toUri()
                    )
                    folder.imageList.add(imageItem)
                    firebaseApi.addFolderToCloudFirestore(folder)
                    firebaseApi.addImageToFirebaseStorage(folder.folderId, imageItem)
                    _foldersList.updateList()
                    return@launch
                }
            }
            val list = mutableListOf<ImageItem>()
            list.add(ImageItem(imageAddContract.requestOrUri.toUri().lastPathSegment.toString(), imageAddContract.requestOrUri.toUri()))
            _foldersList.addItem(Folder(imageAddContract.folderId, imageAddContract.folderName, list))
        }
    }

    fun addNewFolder(folder: Folder) {
        viewModelScope.launch {
            _foldersList.addItem(folder)
            firebaseApi.addFolderToCloudFirestore(folder)
        }
    }

    //not the best case, need to review
    fun getFolderList() {
        val folderList = mutableListOf<Folder>()
        FirebaseApi.getFirestore().collection("locations").document("streets").get().addOnSuccessListener {
            it.data?.forEach { (key, value) ->
                apply {

                    //start
                    val listImageItem = mutableListOf<ImageItem>()
                    val storageRef = FirebaseApi.getFirebaseStorage().reference.child(key)
                    storageRef.listAll().addOnSuccessListener { listResult ->
                        //сюда летят ссылки на фото и формируется список со ссылками
                        listResult.items.forEach { storageReference ->
                            storageReference.downloadUrl.addOnSuccessListener { uri ->
                                val imageItem = ImageItem(uri.lastPathSegment.toString(), uri)
                                listImageItem.add(imageItem)
                                _foldersList.updateList()
                            }
                        }
                    }
                    //end
                    folderList.add(Folder(key, value.toString(), listImageItem))

                } }
            _foldersList.value = folderList
        }
    }

    fun updateFolderName(folderId: String, folderName: String) {
        viewModelScope.launch {
            _foldersList.value?.forEach {
                if (it.folderId == folderId) {
                    it.folderName = folderName
                    return@forEach
                }
            }
            firebaseApi.updateFolderCloudFirestoreName(folderId, folderName)
        }
    }

    fun deleteImages(folderId: String, deletingImageIdsList: List<String>) {
        viewModelScope.launch {
            var bufferImageItem: ImageItem = ImageItem("", Uri.EMPTY)

            _foldersList.value?.forEach { folder ->
                if (folder.folderId == folderId) {
                    folder.imageList.forEach { imageItem ->
                        deletingImageIdsList.forEach {
                            if (it == imageItem.imageId) {
                                firebaseApi.deleteImageFromFirebaseStorage(folderId, imageItem)
                                bufferImageItem = imageItem
                            }
                        }
                    }
                }
                folder.imageList.remove(bufferImageItem)
            }
            _foldersList.updateList()

        }
    }

}