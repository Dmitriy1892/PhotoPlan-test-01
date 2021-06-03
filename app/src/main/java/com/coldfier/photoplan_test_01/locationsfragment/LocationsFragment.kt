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
import com.coldfier.photoplan_test_01.R
import com.coldfier.photoplan_test_01.databinding.LocationsFragmentBinding

class LocationsFragment : Fragment() {

    private val IMAGE_RESPONSE = 1

    companion object {
        fun newInstance() = LocationsFragment()
    }

    private lateinit var viewModel: LocationsViewModel
    private lateinit var binding: LocationsFragmentBinding
    private lateinit var getContent: ActivityResultLauncher<String>
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val src = ImageDecoder.createSource(requireActivity().contentResolver, it)
            bitmap = ImageDecoder.decodeBitmap(src)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = LocationsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)
        binding.addNewFolderFAB.setOnClickListener {
            viewModel.testClick()
            Toast.makeText(context, "Go to firebase!", Toast.LENGTH_SHORT).show()
        }
        binding.include.floatingActionButton.setOnClickListener {
            //viewModel.getClick()
            getContent.launch("image/*")
        }

        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.include.contentNameEditText.setText(it.toString())
        })
        if (bitmap != null) binding.imageView2.setImageBitmap(bitmap)
    }

}