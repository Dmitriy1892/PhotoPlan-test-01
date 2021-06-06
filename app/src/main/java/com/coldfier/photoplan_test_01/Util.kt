package com.coldfier.photoplan_test_01

import androidx.lifecycle.MutableLiveData
import com.coldfier.photoplan_test_01.model.Folder

fun <T> MutableLiveData<List<T>>.addItem(item: T) {
    val oldList = this.value?.toMutableList() ?: mutableListOf()
    oldList.add(item)
    this.value = oldList
}

fun <T> MutableLiveData<List<T>>.deleteItem(item: T) {
    val oldList = this.value?.toMutableList() ?: mutableListOf()
    oldList.remove(item)
    this.value = oldList
}

fun <T> MutableLiveData<List<T>>.updateList() {
    val oldList = this.value
    this.value = oldList
}