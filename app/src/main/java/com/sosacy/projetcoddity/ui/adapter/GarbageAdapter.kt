package com.sosacy.projetcoddity.ui.adapter

import com.sosacy.projetcoddity.R
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import com.sosacy.projetcoddity.data.model.Garbage

import android.database.DataSetObserver
import android.widget.ImageView
import java.util.ArrayList

import androidx.recyclerview.widget.RecyclerView

class GarbageAdapter(
    var garbageList: ArrayList<Garbage>,
) : RecyclerView.Adapter<GarbageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.garbage_list_view, parent, false)
        Log.d("d","bite")
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
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextview: TextView = itemView.findViewById(R.id.titleTextview)
        val coordinatesTextview: TextView = itemView.findViewById(R.id.coordinatesTextview)
    }
}