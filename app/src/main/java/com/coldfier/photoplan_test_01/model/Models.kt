package com.coldfier.photoplan_test_01.model

import android.net.Uri

data class Folder(
    val folderId: String,
    val folderName: String,
    val imageList: MutableList<ImageItem>
)

data class ImageItem(
    val imageId: String,
    val imageUri: Uri
)

//нужен для запоминания id папки. в которую нужно добавить изображение
data class ImageAddContract(
    var folderId: String,
    var folderName: String,
    val requestOrUri: String
)