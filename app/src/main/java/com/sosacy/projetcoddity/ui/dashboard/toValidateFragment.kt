package com.sosacy.projetcoddity.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sosacy.projetcoddity.R
import com.sosacy.projetcoddity.data.model.Garbage
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.databinding.FragmentToValidateBinding
import com.sosacy.projetcoddity.ui.adapter.GarbageAdapter


class toValidateFragment : Fragment() {
    private val SHARED_PREF_USER = "SHARED_PREF_USER"
    private val SHARED_PREF_GARBAGES = "SHARED_PREF_GARBGAES"
    private var _binding: FragmentToValidateBinding? = null
    private val binding get() = _binding!!
    private var SM: SendMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentToValidateBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_to_validate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var recyclerView: RecyclerView = view.findViewById(R.id.garbage_list)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        /* Retrieve stored garbages */
        val mPrefs = this.requireActivity().getSharedPreferences(
            SHARED_PREF_USER,
            AppCompatActivity.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = mPrefs.getString(SHARED_PREF_GARBAGES, null)
        var garbageList: GarbageList = gson.fromJson(json, GarbageList::class.java)

        /* Add garbages on the view */
        println(recyclerView)
        recyclerView.adapter = GarbageAdapter(garbageList.all)

        Log.d("size",garbageList.all.size.toString())
        Log.d("garbageList",garbageList.all[garbageList.all.size-1].latitude.toString())
        Log.d("garbageList",garbageList.all[garbageList.all.size-1].longitude.toString())
        Log.d("garbageList",garbageList.all[garbageList.all.size-1].id.toString())
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        SM = try {
//            activity as SendMessage?
//        } catch (e: ClassCastException) {
//            throw ClassCastException("Error in retrieving data. Please try again")
//        }
//    }

    internal interface SendMessage {
        fun sendData(message: String?)
    }


}