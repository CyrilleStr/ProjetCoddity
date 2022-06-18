package com.sosacy.projetcoddity.ui.adapter

import com.sosacy.projetcoddity.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sosacy.projetcoddity.data.model.Garbage
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Convert garabgeList object in list view
 *
 * @property garbageList
 * @property activity
 */
class GarbageAdapterHistory(
    var garbageList: ArrayList<Garbage>,
    var activity:FragmentActivity
) : RecyclerView.Adapter<GarbageAdapterHistory.ViewHolder>() {

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
}