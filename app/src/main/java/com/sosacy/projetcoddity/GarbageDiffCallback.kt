package com.yuyakaido.android.cardstackview.sample

import androidx.recyclerview.widget.DiffUtil
import com.sosacy.projetcoddity.data.model.Garbage

class GarbageDiffCallback(
    private val old: List<Garbage>,
    private val new: List<Garbage>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
