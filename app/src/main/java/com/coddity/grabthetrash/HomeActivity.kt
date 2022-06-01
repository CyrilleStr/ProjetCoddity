package com.coddity.grabthetrash

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.coddity.grabthetrash.web.WebClient
import com.coddity.grabthetrash.web.network.Result
import com.coddity.grabthetrash.web.network.ServiceBuilder
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.*


class HomeActivity : AppCompatActivity() {

    private lateinit var pictureImgVw: ImageView
    private lateinit var openCameraBtn: Button
    private lateinit var addGarbageBtn: Button
    private lateinit var addBinBtn: Button
    private lateinit var textView: TextView
    private lateinit var addItemSelection: ConstraintLayout
    private lateinit var photo:Bitmap
    private lateinit var imageData: ByteArray
    private lateinit var picturePath:String
    private var fileUri:Uri? = null
//    private lateinit var uri:Uri
    private val PIC_ID = 123

    private val SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO"
    object KeyPreferences{
        const val PICTURE_IS_TAKEN = "PICTURE_IS_TAKEN_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        pictureImgVw = findViewById(R.id.imageView)
        openCameraBtn = findViewById(R.id.openCameraBtn)
        addGarbageBtn = findViewById(R.id.addGarbageBtn)
        addBinBtn = findViewById(R.id.addBinBtn)
        textView = findViewById(R.id.trashState)
        addItemSelection = findViewById<ConstraintLayout>(R.id.addItemSelection)

        TrashOnTheWay(textView)

        openCameraBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                // Check Camera
                if (applicationContext.packageManager.hasSystemFeature(
                        PackageManager.FEATURE_CAMERA
                    )
                ) {
                    // Open default camera
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    val imagesFolder = File(Environment.getExternalStorageDirectory(), "MyImages")
//                    imagesFolder.mkdirs()
//
//                    val image = File("data/", "image_001.jpg")
//                    fileUri = Uri.fromFile(image)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

                    // start the image capture Intent
                    startActivityForResult(intent, PIC_ID)
                } else {
                    Toast.makeText(application, "Camera not supported", Toast.LENGTH_LONG).show()
                }
            }
        })
        addGarbageBtn.setOnClickListener { TrashOnTheWay(textView) }
        addBinBtn.setOnClickListener { }

        addGarbageBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
//                var garbageAttributes = JSONObject()
//                garbageAttributes.put("owner", "1")
//                garbageAttributes.put("latitude", "1.12356")
//                garbageAttributes.put("longitude", "2.0213")
                var garbageAttributes : MutableMap<String,String> = HashMap()
                garbageAttributes["owner"] ="1"
                garbageAttributes["latitude"] = "1.1255"
                garbageAttributes["longitude"] = "2.65456"
                /* Format photo for upload */
//                var imageData:ByteArray? = null
//                val inputStream = contentResolver.openInputStream(uri)
//                inputStream?.buffered()?.use {
//                    imageData = it.readBytes()
//                }
                /* Call webclient for request */
//                val bitmap = Bitmap.createScaledBitmap(photo, 150, 150, false)
//                val outStream = ByteArrayOutputStream()
//
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outStream)
//                val f = File(
//                    Environment.getExternalStorageDirectory()
//                        .toString() + File.separator + "filename.jpg"
//                )
//                f.createNewFile()
//
//                try {
//                    val fo = FileOutputStream(f)
//                    fo.write(outStream.toByteArray())
//                    fo.flush()
//                    fo.close()
//                } catch (e: FileNotFoundException) {
//                    Log.w("TAG", "Error saving image file: " + e.message)
//                } catch (e: IOException) {
//                    Log.w("TAG", "Error saving image file: " + e.message)
//                }
//                val size: Int = photo.getRowBytes() * photo.getHeight()
//                val byteBuffer: ByteBuffer = ByteBuffer.allocate(size)
//                photo.copyPixelsToBuffer(byteBuffer)
//                imageData = byteBuffer.array()
//                var file = File("image")
//                file.writeBytes(imageData)
//                var uri:Uri = file.toUri()
//                doRequest(f,f.toUri())
                WebClient(applicationContext).uploadImage(/*picturePath,*/garbageAttributes,photo)
            }
        })


        /** Setup bottom navigation **/
        // Get View
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

    private fun doRequest(file:File,uri: Uri) {

        val requestFile = file.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val serviceBuilder = ServiceBuilder.myApi
        serviceBuilder.uploadImage(body).enqueue(object : retrofit2.Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                val responseCode = response.code()
                if (responseCode == 200) {
                    Toast.makeText(applicationContext, "Uploaded !", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed due ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    /**
     * Get and display the taken picture
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        // Match the request 'pic id with requestCode
        if (requestCode == PIC_ID) {

            // Cursor to get image uri to display
//            var fileUri:Uri = fileUri!!
//
//            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//            val cursor = contentResolver.query(
//                fileUri,
//                filePathColumn, null, null, null
//            )
//            cursor!!.moveToFirst()
//
//            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//            picturePath = cursor.getString(columnIndex)
//            cursor.close()

            // Display photo
            photo = (data?.extras?.get("data") as Bitmap?)!!
            pictureImgVw.setImageBitmap(photo)

            /* Make addBinBtn and addGarbageBtn visible */
            addItemSelection.visibility = View.VISIBLE
            getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).edit().putInt(KeyPreferences.PICTURE_IS_TAKEN, 1).apply()
        }
    }

    fun TrashOnTheWay(textView: TextView) {
        if (getSharedPreferences(
                SHARED_PREF_USER_INFO,
                MODE_PRIVATE
            ).getInt(KeyPreferences.PICTURE_IS_TAKEN, 0) == 1
        ) {
            textView.isEnabled = true
            textView.text = "Vous êtes entrain de jeter un déchet"
            startService(Intent(this, BackgroundLocationUpdateService::class.java))
        }
    }

    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    fun askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
                return
            }
        }
    }
}