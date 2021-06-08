package com.coldfier.photoplan_test_01.locationsfragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.LocationsFragmentBinding
import com.coldfier.photoplan_test_01.model.Folder
import com.coldfier.photoplan_test_01.model.ImageAddContract

class LocationsFragment : Fragment(), AdapterCallbackInterface {

    private lateinit var viewModel: LocationsViewModel
    private lateinit var binding: LocationsFragmentBinding
    private lateinit var getContent: ActivityResultLauncher<ImageAddContract>
    private var imageAddContract: ImageAddContract? = null
    private lateinit var foldersRVAdapter: FolderListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contract = object :ActivityResultContract<ImageAddContract, ImageAddContract>() {
            override fun createIntent(context: Context, input: ImageAddContract?): Intent {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = input?.requestOrUri
                intent.putExtra("FOLDER_NAME", input?.folderId)
                return intent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): ImageAddContract? {
                if (resultCode == Activity.RESULT_OK || intent != null) {
                    return ImageAddContract(
                        folderId = "",
                        folderName = "",
                        requestOrUri = intent?.data.toString()
                    )
                }
                return null
            }

        }
        getContent = registerForActivityResult(contract) {
            imageAddContract = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LocationsFragmentBinding.inflate(inflater, container, false)
        val viewModelFactory = LocationsViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LocationsViewModel::class.java)

        foldersRVAdapter = FolderListAdapter(getContent, this)
        binding.foldersRecyclerView.adapter = foldersRVAdapter
        binding.foldersRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        viewModel.foldersList.observe(viewLifecycleOwner, {
            foldersRVAdapter.foldersList = it
        })

        binding.mainClickableLayout.setOnClickListener {
            foldersRVAdapter.folderListAdapterObserver.value = false
        }

        binding.addNewFolderFAB.setOnClickListener {
            viewModel.addNewFolder(folder = Folder("abc${(Math.random()*253).toString().replace(".", "")}abc", "Название локации", mutableListOf()))
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (imageAddContract != null) {
            imageAddContract?.folderId = viewModel.folderId!!
            imageAddContract?.folderName = viewModel.folderName!!
            viewModel.addImage(imageAddContract!!)
            imageAddContract = null
            viewModel.folderId = null
            viewModel.folderName = null
        }
    }

    override fun onClickListener(folderId: String, folderName: String) {
        viewModel.folderId = folderId
        viewModel.folderName = folderName
    }

    override fun folderNameChanged(folderId: String, folderName: String) {
        viewModel.updateFolderName(folderId, folderName)
    }

    override fun hideKeyboard(v: View) {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun deletePickedImages(folderId: String, deletingImagesList: List<String>) {
        viewModel.deleteImages(folderId, deletingImagesList)
    }
}