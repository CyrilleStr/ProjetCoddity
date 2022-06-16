package com.sosacy.projetcoddity.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sosacy.projetcoddity.R
import com.sosacy.projetcoddity.data.LocalStorage
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.databinding.FragmentToThrowBinding
import com.sosacy.projetcoddity.ui.adapter.GarbageAdapter
import com.sosacy.projetcoddity.web.WebClient


class ToThrowFragment : Fragment() {
    private lateinit var garbageList: GarbageList
    private lateinit var loading:ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_to_throw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* Initialize fragment */
        recyclerView = view.findViewById(R.id.garbage_list_to_throw)
        loading = view.findViewById(R.id.loading)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        currentView = view

        /* Retrieve data from server */
        retrieveDataFromServer()
    }

    override fun onResume() {
        super.onResume()

        /* If a new garbage was added in the meantime, we refresh the list the page */
        if(LocalStorage.instance.garbageAdded){
            /* Update animation view */
            loading.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            /* Retrieve data from server */
            retrieveDataFromServer()

            /* Update garbage added status */
            LocalStorage.instance.garbageAdded = false
        }
    }

    private fun retrieveDataFromServer(){
        WebClient(requireContext()).getGarbagesToThrow() { response ->
            garbageList = GarbageList()
            garbageList.parseJson(response.toString())

            /* Add garbages on the view */
            if (garbageList.all.size > 0)
                recyclerView.adapter = GarbageAdapter(garbageList.all)

            /* Update layout */
            var loading = currentView.findViewById<ProgressBar>(R.id.loading)
            loading.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            /* Add button listener for updating location */
        }
    }
}