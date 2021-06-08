package com.coldfier.photoplan_test_01.locationsfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.FolderContentBinding
import com.coldfier.photoplan_test_01.model.Folder
import com.coldfier.photoplan_test_01.model.ImageAddContract

class FolderListAdapter
        constructor(private val getContent: ActivityResultLauncher<ImageAddContract>,
                    private val adapterCallbackInterface: AdapterCallbackInterface) : RecyclerView.Adapter<FolderListAdapter.FolderListHolder>() {

    var foldersList = mutableListOf<Folder>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val folderListAdapterObserver = MutableLiveData<Boolean>()

    init {
        folderListAdapterObserver.value = false
    }

    class FolderListHolder(private val binding: FolderContentBinding,
                           private val getContent: ActivityResultLauncher<ImageAddContract>,
                           private val adapterCallbackInterface: AdapterCallbackInterface,
                           private val folderListAdapterObserver: MutableLiveData<Boolean>,
    ): RecyclerView.ViewHolder(binding.root), FolderListCallbackInterface {

        private var deletingMap = mutableMapOf<Int, String>()

        fun bind(folder: Folder) {

            val deleteButtonObserver = MutableLiveData<Boolean>()
            deleteButtonObserver.value = false
            deleteButtonObserver.observeForever {
                if (it) {
                    binding.deleteButton.apply {
                        visibility = View.VISIBLE
                        isClickable = true
                    }

                } else {
                    binding.deleteButton.apply {
                        visibility = View.GONE
                        isClickable = false
                    }
                    deletingMap = mutableMapOf()
                }
            }

            val subRVAdapter = ContentListAdapter(folder.imageList, deleteButtonObserver, this)
            val layoutManager = GridLayoutManager(binding.root.context, 3, RecyclerView.VERTICAL, false)
            layoutManager.initialPrefetchItemCount = folder.imageList.size

            binding.imagesRecyclerView.apply {
                adapter = subRVAdapter
                this.layoutManager = layoutManager
            }

            binding.contentNameEditText.apply {
                setText(folder.folderName)
                setOnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        adapterCallbackInterface.hideKeyboard(v)
                        adapterCallbackInterface.folderNameChanged(folder.folderId, binding.contentNameEditText.text.toString())
                    }

                }
                doAfterTextChanged {

                }
            }

            binding.addImageFAB.setOnClickListener {
                adapterCallbackInterface.onClickListener(folder.folderId, folder.folderName)
                getContent.launch(ImageAddContract("", "", "image/*"))
            }

            folderListAdapterObserver.observeForever {
                subRVAdapter.onFolderFocused.value = it
                deleteButtonObserver.value = it
            }

            binding.clickableLayout.setOnClickListener {
                subRVAdapter.onFolderFocused.value = false
                folderListAdapterObserver.value = false
                deleteButtonObserver.value = false
            }

            binding.deleteButton.setOnClickListener {
                val deletingImagesList = mutableListOf<String>()
                deletingMap.forEach { (key, value) ->
                    binding.imagesRecyclerView.removeViewAt(key)
                    deletingImagesList.add(value)
                }
                adapterCallbackInterface.deletePickedImages(folder.folderId, deletingImagesList)
                folderListAdapterObserver.value = false
            }



            binding.executePendingBindings()
        }

        override fun submitListToDelete(deletingMap: MutableMap<Int, String>) {
            this.deletingMap = deletingMap
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderListHolder {
        return FolderListHolder(
            FolderContentBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            getContent,
            adapterCallbackInterface,
            folderListAdapterObserver)
    }

    override fun onBindViewHolder(holder: FolderListHolder, position: Int) {
        holder.bind(foldersList[position])
    }

    override fun getItemCount(): Int {
        return foldersList.size
    }


}