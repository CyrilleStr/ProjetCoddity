package com.sosacy.projetcoddity.ui.adapter

import com.sosacy.projetcoddity.R
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sosacy.projetcoddity.data.model.Garbage
import android.location.Location
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sosacy.projetcoddity.web.WebClient
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import com.android.volley.Response

/**
 * Convert garbages object in list view
 *
 * @property garbageList
 * @property activity
 * @property responseListener
 */
class GarbageAdapterToThrow(
    var garbageList: ArrayList<Garbage>,
    var activity: FragmentActivity,
    var responseListener: Response.Listener<String>
) : RecyclerView.Adapter<GarbageAdapterToThrow.ViewHolder>() {

    //location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var binCoordinates: JSONArray? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.garbage_list_view_to_throw, parent, false)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity.applicationContext) //location
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // link element view to garbage attribute
        val garbage = garbageList[position]
        holder.imageView.setImageResource(R.drawable.trash)
        holder.titleTextview.text = "Garbage n°" + garbage.id.toString()
        holder.coordinatesTextview.text =
            "(" + garbage.latitude.toString() + ", " + garbage?.longitude.toString() + ")"
        holder.progressBar.visibility = View.GONE
        holder.throwBtn.setOnClickListener() {
            holder.progressBar.visibility = View.VISIBLE
            holder.throwBtn.visibility = View.INVISIBLE
            checkLocation(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return garbageList.size
    }

    /**
     * Check location with fusedLocation
     *
     * @param holder
     * @param position
     */
    private fun checkLocation(holder: ViewHolder, position: Int) {
        if (ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                GarbageAdapterToThrow.LOCATION_PERMISSION_REQUEST_CODE
            )
            println("test")
            holder.progressBar.visibility = View.GONE
            holder.throwBtn.visibility = View.VISIBLE
            holder.throwBtn.text = "Fail"
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(activity) { location ->
            if (location != null)
                lastLocation = location
        }

        WebClient(activity.applicationContext).getBinCoordinates { response ->
            if (response != null) {
                /** On request reponse **/
                /* Check if user location is next to a bin */
                binCoordinates = JSONArray(response.toString())
                var garbageThrown = false
                for (i in 0 until binCoordinates!!.length()) {
                    val coord: JSONObject = binCoordinates!!.getJSONObject(i)
                    var trash_loc = Location("")
                    trash_loc.latitude = coord.get("latitude") as Double
                    trash_loc.longitude = coord.get("longitude") as Double
                    val distance = 0.001
                    if (lastLocation.latitude < trash_loc.latitude + distance
                        && lastLocation.latitude > trash_loc.latitude - distance
                        && lastLocation.longitude < trash_loc.longitude + distance
                        && lastLocation.longitude > trash_loc.longitude - distance
                    ) {
                        holder.progressBar.visibility = View.GONE
                        holder.throwBtn.visibility = View.VISIBLE
                        garbageThrown = true
                        WebClient(activity.applicationContext).throwGarbage(garbageList[position].id,responseListener)
                    }
                }
                if (!garbageThrown) {
                    holder.progressBar.visibility = View.GONE
                    holder.throwBtn.visibility = View.VISIBLE
                    holder.throwBtn.text = "Fail"
                }

            }
        }

    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextview: TextView = itemView.findViewById(R.id.titleTextview)
        val coordinatesTextview: TextView = itemView.findViewById(R.id.coordinatesTextview)
        val throwBtn: Button = itemView.findViewById(R.id.throwBtn)
        val progressBar: ProgressBar = itemView.findViewById(R.id.loading_garbage_list_view)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
    }
}