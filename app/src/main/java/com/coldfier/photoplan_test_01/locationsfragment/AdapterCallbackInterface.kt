package com.coldfier.photoplan_test_01.locationsfragment

import android.view.View

interface AdapterCallbackInterface {
    fun onClickListener(folderId: String, folderName: String)

    fun folderNameChanged(folderId: String, folderName: String)

    fun hideKeyboard(v: View)
}