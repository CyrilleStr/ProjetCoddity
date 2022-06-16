package com.sosacy.projetcoddity.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sosacy.projetcoddity.R
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.ui.adapter.GarbageAdapter
import com.sosacy.projetcoddity.web.WebClient

class HistoryFragment : Fragment() {
    private lateinit var garbageList: GarbageList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var recyclerView: RecyclerView = view.findViewById(R.id.garbage_list_history)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        /* Retrieve data from server */
        WebClient(requireContext()).getGarbagesThrown() { response ->
            garbageList = GarbageList()
            garbageList.parseJson(response.toString())

            /* Add garbages on the view */
            if (garbageList.all.size > 0) {
                println(recyclerView)
                recyclerView.adapter = GarbageAdapter(garbageList.all)
            }
        }
    }
}