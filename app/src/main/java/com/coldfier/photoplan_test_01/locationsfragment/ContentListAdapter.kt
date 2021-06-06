package com.coldfier.photoplan_test_01.locationsfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.ItemImageBinding
import com.coldfier.photoplan_test_01.model.ImageItem

class ContentListAdapter(imageItemList: MutableList<ImageItem>): RecyclerView.Adapter<ContentListAdapter.ContentViewHolder>() {

    var imageItemList = imageItemList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ContentViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(imageItem: ImageItem) {
            binding.uri = imageItem.imageUri
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(imageItemList[position])
    }

    override fun getItemCount(): Int {
        return imageItemList.size
    }


}