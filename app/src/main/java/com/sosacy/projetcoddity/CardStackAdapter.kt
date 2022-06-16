package com.yuyakaido.android.cardstackview.sample

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sosacy.projetcoddity.R
import com.sosacy.projetcoddity.data.model.Garbage

class CardStackAdapter(
    private var garbages: List<Garbage> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.garbage_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val garbage = garbages[position]
        holder.name.text = "garbage id: ${garbage.id}"
        holder.city.text = "location: " + garbage.latitude.toString() + " " + garbage.longitude.toString()
        Glide.with(holder.image)
            .load(garbage.randomImage())
            .into(holder.image)

        Log.i("garbage url:",garbage.randomImage())
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, garbage.id, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return garbages.size
    }

    fun setGarbages(garbages: List<Garbage>) {
        this.garbages = garbages
    }

    fun getGarbages(): List<Garbage> {
        return garbages
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var city: TextView = view.findViewById(R.id.item_city)
        var image: ImageView = view.findViewById(R.id.item_image)
    }

}
