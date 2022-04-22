package com.coddity.grabthetrash

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {

    private lateinit var pictureImgVw: ImageView
    private lateinit var openCameraBtn: Button
    private lateinit var textView: TextView
    private val PIC_ID = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        pictureImgVw = findViewById(R.id.imageView)
        openCameraBtn = findViewById(R.id.openCameraBtn)
        textView = findViewById(R.id.trashState)

        TrashOnTheWay(textView)

        openCameraBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,PIC_ID)
            }
        })

        /** Setup bottom navigation **/
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.navigation_home
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_account -> {
                    startActivity(Intent(applicationContext, AccountActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_home -> return@OnNavigationItemSelectedListener true
                R.id.maps -> {
                    startActivity(Intent(applicationContext, MapsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    /**
     * Get and display the taken picture
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        // Match the request 'pic id with requestCode
        if (requestCode == PIC_ID) {
            val photo = data?.extras
                ?.get("data") as Bitmap?
            pictureImgVw.setImageBitmap(photo)
        }
    }

    fun TrashOnTheWay(textView: TextView){
        textView.isEnabled = true
        textView.text="Vous êtes entrain de jeter un déchet"
    }
}