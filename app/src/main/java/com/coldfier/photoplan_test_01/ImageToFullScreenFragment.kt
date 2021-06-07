package com.coldfier.photoplan_test_01

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coldfier.photoplan_test_01.databinding.FragmentImageToFullScreenBinding

class ImageToFullScreenFragment : Fragment() {

    private lateinit var binding: FragmentImageToFullScreenBinding
    private lateinit var imgUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imgUri = ImageToFullScreenFragmentArgs.fromBundle(requireArguments()).imageUri
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentImageToFullScreenBinding.inflate(inflater, container, false)
        binding.fullScreenUri = imgUri
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}