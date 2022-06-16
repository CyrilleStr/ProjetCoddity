package com.sosacy.projetcoddity.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.sosacy.projetcoddity.MapsFragment
import com.sosacy.projetcoddity.data.LocalStorage
import com.sosacy.projetcoddity.data.model.BinList
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.data.model.Garbage
import com.sosacy.projetcoddity.databinding.FragmentHomeBinding
import com.sosacy.projetcoddity.web.WebClient
import org.json.JSONObject


class HomeFragment : Fragment() {

    private lateinit var applicationContext: Context
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val PIC_ID = 123
    private val SHARED_PREF_USER = "SHARED_PREF_USER"
    private val SHARED_PREF_USER_ID = "SHARED_PREF_USER_ID"
    private val SHARED_PREF_GARBAGES = "SHARED_PREF_GARBGAES"
    private val SHARED_PREF_GARBAGE_TO_VALIDATE = "SHARED_PREF_GARBAGE_TO_VALIDATE"
    private val PHOTO_KEY = "pictureImgVw"
    private lateinit var pictureImgVw: ImageView
    private lateinit var openCameraBtn: Button
    private lateinit var addGarbageBtn: Button
    private lateinit var addBinBtn: Button
    private lateinit var msgTextView: TextView
    private lateinit var addItemSelection: ConstraintLayout
    private lateinit var applicationActivity: FragmentActivity

    //location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        applicationActivity = this.requireActivity()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        pictureImgVw = binding.imageView
        openCameraBtn = binding.openCameraBtn
        addGarbageBtn = binding.addGarbageBtn
        addBinBtn = binding.addBinBtn
        addItemSelection = binding.addItemSelection
        msgTextView = binding.msgTextView
        applicationContext = this.requireActivity().applicationContext
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext) //location

        /* Add button listeners */
        openCameraBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Check Cameraf
                if (applicationContext.packageManager.hasSystemFeature(
                        PackageManager.FEATURE_CAMERA
                    )
                ) {
                    // Open default camera
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, PIC_ID)
                    getLocation()
                } else {
                    Toast.makeText(context, "Camera not supported", Toast.LENGTH_LONG).show()
                }
            }
        })

        addGarbageBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                addItemSelection.visibility = View.INVISIBLE
                pictureImgVw.visibility = View.INVISIBLE
                getLocation()
                WebClient(applicationContext).addGarbage(
                    lastLocation.latitude.toString(), lastLocation.longitude.toString(),
                    {
                        /* Manage view animation */
                        msgTextView.setText("Congratulations ! Walk next to the nearest bin referenced on the map to validate the garbage")
                        msgTextView.visibility = View.VISIBLE
                        openCameraBtn.visibility = View.VISIBLE

                        /* Change garbage status : notify that a garbage has been added */
                        LocalStorage.instance.garbageAdded = true
                    },
                    {
                        Log.d("debug", "errorListener")
                        msgTextView.setText("Connection error, try again")
                        msgTextView.visibility = View.VISIBLE
                        openCameraBtn.visibility = View.VISIBLE
                    }
                )
            }
        })

        addBinBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var garbageAttributes = JSONObject()
                garbageAttributes.put("owner", "1")
                garbageAttributes.put("latitude", "14")
                garbageAttributes.put("longitude", "254")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_ID) {
            /* Display photo and display add-buttons */
            if (data != null) {
                val photo: Bitmap = data?.extras?.get("data") as Bitmap
                pictureImgVw.setImageBitmap(photo)
                pictureImgVw.visibility = View.VISIBLE
                msgTextView.visibility = View.INVISIBLE
                openCameraBtn.visibility = View.INVISIBLE
                addItemSelection.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
    }

    /*
     * Permit the location when we take a picture
     */

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(applicationActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                HomeFragment.LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(applicationActivity) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null)
                lastLocation = location
        }
    }
}