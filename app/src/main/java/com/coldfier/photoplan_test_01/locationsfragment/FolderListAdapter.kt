package com.coldfier.photoplan_test_01.locationsfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.FolderContentBinding
import com.coldfier.photoplan_test_01.model.Folder
import com.coldfier.photoplan_test_01.model.ImageAddContract

class FolderListAdapter
        constructor(private val getContent: ActivityResultLauncher<ImageAddContract>,
                    private val folderClickListener: FolderClickListener) : RecyclerView.Adapter<FolderListAdapter.FolderListHolder>() {

    var foldersList = listOf<Folder>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class FolderListHolder(private val binding: FolderContentBinding, private val getContent: ActivityResultLauncher<ImageAddContract>, private val folderClickListener: FolderClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: Folder) {
            val subRVAdapter = ContentListAdapter(folder.imageList)
            val layoutManager = GridLayoutManager(binding.root.context, 3, RecyclerView.VERTICAL, false)
            layoutManager.initialPrefetchItemCount = folder.imageList.size
            binding.imagesRecyclerView.adapter = subRVAdapter
            binding.imagesRecyclerView.layoutManager = layoutManager
            binding.contentNameEditText.setText(folder.folderName)
            binding.addImageFAB.setOnClickListener {
                folderClickListener.onClickListener(folder.folderId, folder.folderName)
                getContent.launch(ImageAddContract(binding.contentNameEditText.text.toString(), "", "image/*"))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderListHolder {
        return FolderListHolder(FolderContentBinding.inflate(LayoutInflater.from(parent.context), parent, false), getContent, folderClickListener)
    }

    override fun onBindViewHolder(holder: FolderListHolder, position: Int) {
        holder.bind(foldersList[position])
    }

    override fun getItemCount(): Int {
        return foldersList.size
    }


}