package com.coldfier.photoplan_test_01.locationsfragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.ItemImageBinding
import com.coldfier.photoplan_test_01.model.ImageItem

class ContentListAdapter(imageItemList: MutableList<ImageItem>): RecyclerView.Adapter<ContentListAdapter.ContentViewHolder>() {

    private var imageItemList = imageItemList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ContentViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(imageItem: ImageItem) {
            binding.uri = imageItem.imageUri
            var isImageFitToScreen = false
            binding.holderImageView.setOnClickListener {
                val action = LocationsFragmentDirections.actionLocationsFragmentToImageToFullScreenFragment(binding.uri as Uri)
                it.findNavController().navigate(action)
            }
            binding.holderImageView.setOnLongClickListener {
                binding.deleteCheckBox.visibility = View.VISIBLE
                true
            }

            //дописать логику - обратный вызов в FolderViewHolder для пометки к удалению картинки по uri
            binding.deleteCheckBox.setOnCheckedChangeListener { buttonView, isChecked -> if (isChecked) binding.uri }

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