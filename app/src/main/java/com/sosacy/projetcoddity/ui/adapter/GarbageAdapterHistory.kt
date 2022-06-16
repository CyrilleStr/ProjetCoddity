package com.sosacy.projetcoddity.ui.adapter

import com.sosacy.projetcoddity.R
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sosacy.projetcoddity.data.model.Garbage

import android.database.DataSetObserver
import android.location.Location
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.core.Progress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.sosacy.projetcoddity.ui.home.HomeFragment
import com.sosacy.projetcoddity.web.WebClient
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class GarbageAdapterHistory(
    var garbageList: ArrayList<Garbage>,
    var activity:FragmentActivity
) : RecyclerView.Adapter<GarbageAdapterHistory.ViewHolder>() {

    //location

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.garbage_list_view_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val garbage = garbageList[position]
        holder.imageView.setImageResource(R.drawable.trash)
        holder.titleTextview.text = "Garbage n°" + garbage.id.toString()
        holder.coordinatesTextview.text = "(" + garbage.latitude.toString() + ", " + garbage?.longitude.toString() + ")"
    }

    override fun getItemCount(): Int {
        return garbageList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewHistory)
        val titleTextview: TextView = itemView.findViewById(R.id.titleTextview)
        val coordinatesTextview: TextView = itemView.findViewById(R.id.coordinatesTextview)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
    }
}