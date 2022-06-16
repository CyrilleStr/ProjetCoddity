package com.sosacy.projetcoddity

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.sosacy.projetcoddity.data.model.GarbageList
import com.sosacy.projetcoddity.databinding.ActivityMainBinding
import com.sosacy.projetcoddity.web.WebClient

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private val SHARED_PREF_USER = "SHARED_PREF_USER"
    private val SHARED_PREF_USER_ID = "SHARED_PREF_USER_ID"
    private val SHARED_PREF_GARBAGE_TO_VALIDATE = "SHARED_PREF_GARBAGE_TO_VALIDATE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Initialize local storage */
        val mPrefsEditor = this.getSharedPreferences(
            SHARED_PREF_USER,
            AppCompatActivity.MODE_PRIVATE
        ).edit()
        val gson = Gson()
        var garbages = GarbageList()
        var json = gson.toJson(garbages)
        mPrefsEditor.putString("garbgages", json)
        mPrefsEditor.commit()

        /* Initialize view */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        /* Fonction pour samy */
//        var webapp = WebClient(applicationContext)
//        webapp.getGarbagesToRate() {
//            var garbageList = GarbageList()
//            garbageList.parseJson(it.toString())
//            println("garbageList")
//            for (i in 0 until garbageList.all.size) {
//                println(garbageList.all[i].id)
//            }
//        }
//
//        webapp.throwGarbage(47
//        ) {
//            println("throwGarbage")
//            println(it.toString())
//        }
//
//        webapp.rateGarbage(50,2){
//            println("rateGarbage")
//            println(it.toString())
//        }


        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        visibilityNavElements(navController)
    }

    private fun visibilityNavElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> navView?.visibility = View.GONE
                else -> navView?.visibility = View.VISIBLE
            }
        }
    }
}