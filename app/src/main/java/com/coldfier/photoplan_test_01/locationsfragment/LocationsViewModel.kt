package com.coldfier.photoplan_test_01.locationsfragment

import android.app.Application
import android.graphics.*
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

    val appCtx = application

    private var _foldersList = MutableLiveData<List<Folder>>()
    val foldersList: LiveData<List<Folder>>
        get() = _foldersList

    var folderId: String? = null
    var folderName: String? = null


    private val firebaseApi = FirebaseApi()

    init {
        _foldersList.value = listOf()
        _foldersList.addItem(Folder("null", "null", mutableListOf()))
    }

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
                    firebaseApi.addImageToFirebaseStorage(imageItem, folder.folderId)
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

    fun getFoldersList() {
        viewModelScope.launch {
            _foldersList.value = firebaseApi.getFolderListFromCloudFirestore()
        }
    }
}