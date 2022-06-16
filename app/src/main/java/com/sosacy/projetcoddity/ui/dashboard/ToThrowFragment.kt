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
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.databinding.FragmentToThrowBinding
import com.sosacy.projetcoddity.ui.adapter.GarbageAdapter
import com.sosacy.projetcoddity.web.WebClient


class ToThrowFragment : Fragment() {
    private val SHARED_PREF_USER = "SHARED_PREF_USER"
    private val SHARED_PREF_GARBAGES = "SHARED_PREF_GARBGAES"
    private lateinit var garbageList: GarbageList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_to_throw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var recyclerView: RecyclerView = view.findViewById(R.id.garbage_list_to_throw)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        /* Retrieve stored garbages */
        val mPrefs = this.requireActivity().getSharedPreferences(
            SHARED_PREF_USER,
            AppCompatActivity.MODE_PRIVATE
        )
//        val gson = Gson()
//        val json: String? = mPrefs.getString(SHARED_PREF_GARBAGES, null)
//        var garbageList: GarbageList? = gson.fromJson(json, GarbageList::class.java)

        /* Retrieve data from server */
        WebClient(requireContext()).getGarbagesToThrow() { response ->
            Log.d("getgarbage","bite")
            garbageList = GarbageList()
            garbageList.parseJson(response.toString())

            /* Add garbages on the view */
            if (garbageList.all.size > 0) {
                println(recyclerView)
                recyclerView.adapter = GarbageAdapter(garbageList.all)
            }

            /* Hide progress bar */
            var loading = view.findViewById<ProgressBar>(R.id.loading)
            loading.visibility = View.GONE
        }
    }
}