package com.coldfier.photoplan_test_01

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<List<T>>.addItem(item: T) {
    val oldList = this.value?.toMutableList() ?: mutableListOf()
    oldList.add(item)
    this.value = oldList
}