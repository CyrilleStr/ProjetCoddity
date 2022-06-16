package com.sosacy.projetcoddity.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sosacy.projetcoddity.data.model.BinList
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.data.model.Garbage
import com.sosacy.projetcoddity.databinding.FragmentHomeBinding
import com.sosacy.projetcoddity.web.WebClient
import org.json.JSONObject


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val PIC_ID = 123
    private val SHARED_PREF_USER = "SHARED_PREF_USER"
    private val SHARED_PREF_USER_ID = "SHARED_PREF_USER_ID"
    private val SHARED_PREF_GARBAGES = "SHARED_PREF_GARBGAES"
    private val SHARED_PREF_GARBAGE_TO_VALIDATE = "SHARED_PREF_GARBAGE_TO_VALIDATE"
    private val PHOTO_KEY = "pictureImgVw"
    private lateinit var pictureImgVw: ImageView
    private lateinit var openCameraBtn: Button
    private lateinit var addGarbageBtn: Button
    private lateinit var addBinBtn: Button
    private lateinit var msgTextView: TextView
    private lateinit var addItemSelection: ConstraintLayout
    private lateinit var applicationActivity: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("debug", "onCreateView")
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        applicationActivity = this.requireActivity()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("debug", "onViewCreated")

        super.onViewCreated(view, savedInstanceState)
        pictureImgVw = binding.imageView
        openCameraBtn = binding.openCameraBtn
        addGarbageBtn = binding.addGarbageBtn
        addBinBtn = binding.addBinBtn
        addItemSelection = binding.addItemSelection
        msgTextView = binding.msgTextView
        val context = this.requireActivity().applicationContext

        /* Add button listeners */
        openCameraBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Check Camera
                if (context.packageManager.hasSystemFeature(
                        PackageManager.FEATURE_CAMERA
                    )
                ) {
                    // Open default camera
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, PIC_ID)
                } else {
                    Toast.makeText(context, "Camera not supported", Toast.LENGTH_LONG).show()
                }
            }
        })

        addGarbageBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                addItemSelection.visibility = View.INVISIBLE
                pictureImgVw.visibility = View.INVISIBLE
                Log.d("debug", "on click addGarbageBtn")
                WebClient(context).addGarbage(
                    "156", "54",
                    {
                        /* Manage view animation */
                        Log.d("debug", "successListener")
                        msgTextView.setText("Congratulations ! Walk next to the nearest bin referenced on the map to validate the garbage")
                        msgTextView.visibility = View.VISIBLE
                        openCameraBtn.visibility = View.VISIBLE

                        /* Add garbage to local storage */
                        addGarbageToLocal(JSONObject(it.toString()))
                    },
                    {
                        Log.d("debug", "errorListener")
                        msgTextView.setText("Connection error, try again")
                        msgTextView.visibility = View.VISIBLE
                        openCameraBtn.visibility = View.VISIBLE
                    }, context
                )
            }
        })

        addBinBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Log.d("debug", "on click addBinBtn")
                var garbageAttributes = JSONObject()
                garbageAttributes.put("owner", "1")
                garbageAttributes.put("latitude", "14")
                garbageAttributes.put("longitude", "254")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_ID) {
            /* Display photo and display add-buttons */
            if (data != null) {
                val photo: Bitmap = data?.extras?.get("data") as Bitmap
                pictureImgVw.setImageBitmap(photo)
                pictureImgVw.visibility = View.VISIBLE
                msgTextView.visibility = View.INVISIBLE
                openCameraBtn.visibility = View.INVISIBLE
                addItemSelection.visibility = View.VISIBLE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("debug", "onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    fun addGarbageToLocal(response: JSONObject) {
        /* Retrieve stored garbages */
        val mPrefs = applicationActivity.getSharedPreferences(
            SHARED_PREF_USER,
            AppCompatActivity.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = mPrefs.getString(SHARED_PREF_GARBAGES, null)
        var garbages = if (json == null)
            GarbageList()
        else
            gson.fromJson(json, GarbageList::class.java)

        /* Add garbage to garbages */
        var garbage = Garbage(
            response.get("id").toString().toInt(),
            response.get("latitude").toString().toFloat(),
            response.get("longitude").toString().toFloat(),
            false,
            false
        )
        garbages.all.add(garbage)
        var prefsEditor = mPrefs.edit()
        var json1 = gson.toJson(garbages)
        prefsEditor.putString(SHARED_PREF_GARBAGES, json1)
        prefsEditor.commit()
    }

}