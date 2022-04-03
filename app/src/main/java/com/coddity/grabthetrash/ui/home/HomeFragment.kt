package com.coddity.grabthetrash.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.coddity.grabthetrash.databinding.FragmentHomeBinding
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var pictureImgVw: ImageView
    private lateinit var openCameraBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pictureImgVw = binding.imageView
        openCameraBtn = binding.openCameraBtn
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            i.text = it
//        }

        /* Request for Camera permission */
        if(ContextCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.CAMERA) !=  PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(),
                arrayOf<String>(Manifest.permission.CAMERA),
                    100)
        }

        openCameraBtn.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 100)
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            pictureImgVw.setImageBitmap(photo)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}