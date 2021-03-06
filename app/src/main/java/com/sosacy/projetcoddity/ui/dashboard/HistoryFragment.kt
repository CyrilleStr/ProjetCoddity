package com.sosacy.projetcoddity.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sosacy.projetcoddity.R
import com.sosacy.projetcoddity.data.LocalStorage
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.ui.adapter.GarbageAdapterHistory
import com.sosacy.projetcoddity.ui.adapter.GarbageAdapterToThrow
import com.sosacy.projetcoddity.web.WebClient

class HistoryFragment : Fragment() {
    private lateinit var garbageList: GarbageList
    private lateinit var loading:ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Initialize */
        recyclerView = view.findViewById(R.id.garbage_list_history)
        loading = view.findViewById(R.id.loading)
        currentView = view
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        /* Retrieve data from server */
        retrieveDataFromServer()
    }

    override fun onResume() {
        super.onResume()
        /* If a new garbage was added in the meantime, we refresh the list the page */
        if(LocalStorage.instance.garbageThrown){
            /* Update animation view */
            loading.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            /* Retrieve data from server */
            retrieveDataFromServer()

            /* Update garbage added status */
            LocalStorage.instance.garbageThrown = false
        }
    }

    private fun retrieveDataFromServer(){
        WebClient(requireContext()).getGarbagesThrown() { response ->
            garbageList = GarbageList()
            garbageList.parseJson(response.toString())

            /* Add garbages on the view */
            if (garbageList.all.size > 0)
                recyclerView.adapter = GarbageAdapterHistory(garbageList.all, this.requireActivity())

            /* Hide progress bar */
            var loading = currentView.findViewById<ProgressBar>(R.id.loading)
            loading.visibility = View.GONE
        }
    }
}