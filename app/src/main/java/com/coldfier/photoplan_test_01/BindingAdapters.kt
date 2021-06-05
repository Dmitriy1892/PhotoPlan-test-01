package com.coldfier.photoplan_test_01

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("setImage")
fun bindImage(imageView: ImageView, imageUri: Uri?) {
    imageUri?.let {
        Glide.with(imageView.context).load(imageUri).into(imageView)
    }
}