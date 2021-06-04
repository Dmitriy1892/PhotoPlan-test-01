package com.coldfier.photoplan_test_01.locationsfragment

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.LocationsFragmentBinding

class LocationsFragment : Fragment() {

    private lateinit var viewModel: LocationsViewModel
    private lateinit var binding: LocationsFragmentBinding
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var bitmap: Bitmap
    private lateinit var bitmapUri: Uri
    private lateinit var rvAdapter: ContentRecyclerViewAdapter

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                bitmapUri = it

                val src = ImageDecoder.createSource(requireContext().contentResolver, bitmapUri)
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


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.addNewFolderFAB.setOnClickListener {
            viewModel.getImageFromFirebase(binding.include.contentNameEditText.text.toString())
        }
        binding.include.floatingActionButton.setOnClickListener {
            getContent.launch("image/*")
        }

        rvAdapter = ContentRecyclerViewAdapter()
        binding.include.imagesRecyclerView.adapter = ContentRecyclerViewAdapter()
        binding.include.imagesRecyclerView.layoutManager = GridLayoutManager(requireContext().applicationContext, 3, RecyclerView.VERTICAL, false)

        viewModel.imageList.observe(viewLifecycleOwner, {
            rvAdapter.imagesList = it
            binding.imageView2.setImageBitmap(it[0])
        })
    }

    override fun onResume() {
        super.onResume()

        if (::bitmap.isInitialized) {
            viewModel.addBitmap(bitmap)
        }

        if (::bitmap.isInitialized && ::bitmapUri.isInitialized) {
            viewModel.addImageToFirebase(bitmap, bitmapUri, binding.include.contentNameEditText.text.toString())
        }
    }

}