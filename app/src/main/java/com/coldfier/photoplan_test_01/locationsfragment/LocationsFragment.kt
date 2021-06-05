package com.coldfier.photoplan_test_01.locationsfragment

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.photoplan_test_01.databinding.LocationsFragmentBinding

class LocationsFragment : Fragment() {

    private lateinit var viewModel: LocationsViewModel
    private lateinit var binding: LocationsFragmentBinding
    private lateinit var getContent: ActivityResultLauncher<String>
    private var bitmap: Bitmap? = null
    private lateinit var rvAdapter: ContentListAdapter//ContentRVAdapter

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

        rvAdapter = ContentListAdapter()
        binding.include.imagesRecyclerView.adapter = rvAdapter
        binding.include.imagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)


        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.include.contentNameEditText.setText(it.toString())
        })

        viewModel.imagesList.observe(viewLifecycleOwner, Observer{
            rvAdapter.submitList(it)
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()


        if (bitmap != null) {
            viewModel.addImage(bitmap!!)
            bitmap = null
        }
    }

}