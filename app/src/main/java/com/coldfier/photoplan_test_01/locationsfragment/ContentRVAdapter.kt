package com.coldfier.photoplan_test_01.locationsfragment

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.ItemImageBinding

class ContentRVAdapter: RecyclerView.Adapter<ContentRVAdapter.ContentViewHolder>(){

    var adapterImagesList = mutableListOf<Bitmap>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ContentViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Bitmap) {
            binding.holderImageView.setImageBitmap(image)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(adapterImagesList[position])
    }

    override fun getItemCount(): Int {
        return adapterImagesList.size
    }

}