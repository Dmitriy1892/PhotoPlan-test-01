package com.coldfier.photoplan_test_01.locationsfragment

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.ItemImageBinding

class ContentListAdapter: ListAdapter<Bitmap, ContentListAdapter.ContentViewHolder>(DIFF_CALLBACK) {

    class ContentViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Bitmap) {
            binding.holderImageView.setImageBitmap(image)
            binding.executePendingBindings()
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Bitmap>() {
            override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
                return oldItem.sameAs(newItem)
            }

            override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
                return oldItem.sameAs(newItem)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}