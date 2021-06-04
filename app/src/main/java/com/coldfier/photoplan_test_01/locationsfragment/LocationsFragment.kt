package com.coldfier.photoplan_test_01.locationsfragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.graphics.decodeBitmap
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.R
import com.coldfier.photoplan_test_01.databinding.LocationsFragmentBinding

class LocationsFragment : Fragment() {

    private lateinit var viewModel: LocationsViewModel
    private lateinit var binding: LocationsFragmentBinding
    private lateinit var getContent: ActivityResultLauncher<String>
    private var bitmap: Bitmap? = null
    private lateinit var rvAdapter: ContentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                val src = ImageDecoder.createSource(requireActivity().contentResolver, it)
                bitmap = ImageDecoder.decodeBitmap(src)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = LocationsFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)

        binding.addNewFolderFAB.setOnClickListener {
            viewModel.testClick()
            Toast.makeText(context, "Go to firebase!", Toast.LENGTH_SHORT).show()
        }
        binding.include.floatingActionButton.setOnClickListener {
            getContent.launch("image/*")
        }

        rvAdapter = ContentRVAdapter()
        binding.include.imagesRecyclerView.adapter = rvAdapter
        binding.include.imagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)


        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.include.contentNameEditText.setText(it.toString())
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()


        if (bitmap != null) {
            viewModel.imagesList.value!!.add(0, bitmap!!)
        }

        viewModel.imagesList.observe(viewLifecycleOwner, {

            if (it.size > 0) {
                rvAdapter.adapterImagesList = it
            }
        })

    }

}