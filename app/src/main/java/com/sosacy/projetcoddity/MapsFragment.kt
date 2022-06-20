package com.sosacy.projetcoddity

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.sosacy.projetcoddity.web.WebClient
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Used to initialize the map, the current location, and to display bin icons on the map
 *
 **/
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var applicationContext: Context
    private var binCoordinates: JSONArray? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        applicationContext = this.requireActivity().applicationContext
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.navigation_maps) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    }
    /**
     * Used to place the bin icon on the map
     * @param location of the bin
     * @param title of the bin
     **/
    private fun placeMarkerOnMap(location: LatLng, title: String) {
        val markerOptions = MarkerOptions().position(location)
        val icon = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("trash",100,100))
        markerOptions.title(title)
        markerOptions.icon(icon)
        mMap.addMarker(markerOptions)
    }

    /**
     * Resize the icons of bin displayed on the map
     * @param iconName of the bin
     * @param width desired for the icon
     * @param height desired for the icon
     **/
    private fun resizeMapIcons(iconName: String?, width: Int, height: Int): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(
            resources, resources.getIdentifier(
                iconName, "drawable", this.requireActivity().packageName
            )
        )
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    companion object {  //Constants of permission used to request the permission for location
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    /**
     * Function called to set up the map :
     *  - Check permission for the localisation
     *  - Check and initialize lastLocation with fusedLocationClient
     *  - Get the coordinates of some bins with a request
     *  - Display on the map a bin icon for every coordinate
     **/
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)

                    /** Get bin coordinates **/
                    WebClient(applicationContext).getBinCoordinates { response ->
                        if (response != null) {
                            binCoordinates = JSONArray(response.toString())
                            Log.d("json coordinates",binCoordinates.toString())

                            for (i in 0 until binCoordinates!!.length()){
                                println("poubelle $i")
                                val coord: JSONObject = binCoordinates!!.getJSONObject(i)
                                Log.e("coordonees!! latitude", coord.get("latitude").toString())
                                Log.e("coordonees!! longitude", coord.get("longitude").toString())
                                var rand_loc = Location("")
                                rand_loc.latitude = coord.get("latitude") as Double
                                rand_loc.longitude= coord.get("longitude") as Double
                                val poubelleMarker = LatLng(rand_loc.latitude, rand_loc.longitude)
                                val title = "Poubelle $i"

                                placeMarkerOnMap(poubelleMarker, title)

                            }

                        }
                    }

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
            }
        }
    }
}