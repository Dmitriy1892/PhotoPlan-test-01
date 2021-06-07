package com.coldfier.photoplan_test_01.locationsfragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.ItemImageBinding
import com.coldfier.photoplan_test_01.model.ImageItem

class ContentListAdapter(imageItemList: MutableList<ImageItem>,
                         private val deleteButtonObserver: MutableLiveData<Boolean>,
                         private val folderListCallbackInterface: FolderListCallbackInterface
                         ): RecyclerView.Adapter<ContentListAdapter.ContentViewHolder>() {

    private var imageItemList = imageItemList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val onFolderFocused = MutableLiveData<Boolean>()

    init {
        onFolderFocused.value = false
    }

    class ContentViewHolder(private val binding: ItemImageBinding,
                            private val onFolderFocused: MutableLiveData<Boolean>,
                            private val deleteButtonObserver: MutableLiveData<Boolean>,
                            private val folderListCallbackInterface: FolderListCallbackInterface): RecyclerView.ViewHolder(binding.root) {

        fun bind(imageItem: ImageItem) {
            var deletingMap = mutableMapOf<Int, String>()

            binding.uri = imageItem.imageUri
            binding.holderImageView.setOnClickListener {
                val action = LocationsFragmentDirections.actionLocationsFragmentToImageToFullScreenFragment(binding.uri as Uri)
                it.findNavController().navigate(action)
            }

            onFolderFocused.observeForever {
                if (it) {
                    binding.deleteCheckBox.visibility = View.VISIBLE
                    binding.deleteCheckBox.isClickable = true
                } else {
                    binding.deleteCheckBox.visibility = View.GONE
                    binding.deleteCheckBox.isClickable = false
                    binding.deleteCheckBox.isChecked = false
                    deletingMap = mutableMapOf()
                    folderListCallbackInterface.submitListToDelete(deletingMap)
                }
            }

            binding.holderImageView.setOnLongClickListener {
                onFolderFocused.value = true
                deleteButtonObserver.value = true
                true
            }

            //дописать логику - обратный вызов в FolderViewHolder для пометки к удалению картинки по uri
            binding.deleteCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    deletingMap[this.adapterPosition] = imageItem.imageId
                    folderListCallbackInterface.submitListToDelete(deletingMap)
                }
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(
            ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onFolderFocused,
            deleteButtonObserver,
            folderListCallbackInterface)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(imageItemList[position])
    }

    override fun getItemCount(): Int {
        return imageItemList.size
    }


}