package com.sosacy.projetcoddity.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.sosacy.projetcoddity.data.LocalStorage
import com.sosacy.projetcoddity.databinding.FragmentHomeBinding
import com.sosacy.projetcoddity.web.WebClient
import org.json.JSONObject


class HomeFragment : Fragment() {

    private lateinit var applicationContext: Context
    private lateinit var applicationActivity: FragmentActivity
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    /* View */
    private val PIC_ID = 123
    private lateinit var pictureImgVw: ImageView
    private lateinit var openCameraBtn: Button
    private lateinit var addGarbageBtn: Button
    private lateinit var addBinBtn: Button
    private lateinit var msgTextView: TextView
    private lateinit var addItemSelection: ConstraintLayout

    /* location */
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
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
        applicationActivity = this.requireActivity()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        /* Add button listeners */
        openCameraBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Check Camera authorization
                if (applicationContext.packageManager.hasSystemFeature(
                        PackageManager.FEATURE_CAMERA
                    )
                ) {
                    // Open default camera
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, PIC_ID)
                    // Get location
                    getLocation()
                } else {
                    Toast.makeText(context, "Camera not supported", Toast.LENGTH_LONG).show()
                }
            }
        })

        addGarbageBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // Manage view animation
                addItemSelection.visibility = View.INVISIBLE
                pictureImgVw.visibility = View.INVISIBLE

                // Get location
                getLocation()

                // Add garbage
                WebClient(applicationContext).addGarbage(
                    lastLocation.latitude.toString(), lastLocation.longitude.toString(),
                    /** On request result **/
                    {
                        /* Manage view animation */
                        msgTextView.setText("Congratulations ! Walk next to the nearest bin referenced on the map to validate the garbage")
                        msgTextView.visibility = View.VISIBLE
                        openCameraBtn.visibility = View.VISIBLE

                        /* Change garbage status : notify that a garbage has been added */
                        LocalStorage.instance.garbageAdded = true
                    },
                    {
                        /* Error message when request failed */
                        msgTextView.setText("Connection error, try again")
                        msgTextView.visibility = View.VISIBLE
                        openCameraBtn.visibility = View.VISIBLE
                    }
                )
            }
        })

        addBinBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                addItemSelection.visibility = View.INVISIBLE
                pictureImgVw.visibility = View.INVISIBLE
                getLocation()
                WebClient(applicationContext).addBin(lastLocation.latitude.toString(),
                    lastLocation.longitude.toString(),
                    /** On request result **/
                    {
                        /* Manage view animation */
                        msgTextView.setText("Congratulations ! Wait for moderators to validate this bin.")
                        msgTextView.visibility = View.VISIBLE
                        openCameraBtn.visibility = View.VISIBLE
                    },
                    {
                        /* Error message when request failed */
                        msgTextView.setText("Connection error, try again")
                        msgTextView.visibility = View.VISIBLE
                        openCameraBtn.visibility = View.VISIBLE
                    }
                )
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Display the picture after the user took it
     *
     * @param requestCode request code
     * @param resultCode result code
     * @param data activity intent
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_ID) {
            // Display photo and display add-buttons
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

    /**
     * Get last location
     *
     */
    private fun getLocation() {
        // Check location permission
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                applicationActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                HomeFragment.LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(applicationActivity) { location ->
            if (location != null)
                lastLocation = location
            else
                requestLocation()
        }
    }

    /**
     * Request location with fusedLocation object
     *
     */
    private fun requestLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("Failed to get location")
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallBack,
            Looper.myLooper()!!
        )
    }

    /**
     * Fusedlocation callback to store the location in lastlocation
     */
    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val location: Location? = p0?.lastLocation

            if (location != null) {
                lastLocation = location
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
    }
}