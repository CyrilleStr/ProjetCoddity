//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.View
//import android.widget.*
//import com.android.volley.RequestQueue
//import com.android.volley.Response
//import com.android.volley.toolbox.Volley
//import com.android.volley.toolbox.StringRequest
//import org.json.JSONObject
//import org.json.JSONException
//import com.android.volley.VolleyError
//import java.util.HashMap
//
//class test : AppCompatActivity() {
//    // creating variables for our edittext,
//    // button, textview and progressbar.
//    private var nameEdt: EditText? = null
//    private var jobEdt: EditText? = null
//    private var postDataBtn: Button? = null
//    private var responseTV: TextView? = null
//    private var loadingPB: ProgressBar? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // initializing our views
//        nameEdt = findViewById(R.id.idEdtName)
//        jobEdt = findViewById(R.id.idEdtJob)
//        postDataBtn = findViewById(R.id.idBtnPost)
//        responseTV = findViewById(R.id.idTVResponse)
//        loadingPB = findViewById(R.id.idLoadingPB)
//
//        // adding on click listener to our button.
//        postDataBtn.setOnClickListener(View.OnClickListener { // validating if the text field is empty or not.
//            if (nameEdt.getText().toString().isEmpty() && jobEdt.getText().toString().isEmpty()) {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Please enter both the values",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return@OnClickListener
//            }
//            // calling a method to post the data and passing our name and job.
//            postDataUsingVolley(nameEdt.getText().toString(), jobEdt.getText().toString())
//        })
//    }
//
//    private fun postDataUsingVolley(name: String, job: String) {
//        // url to post our data
//        val url = "https://reqres.in/api/users"
//        loadingPB!!.visibility = View.VISIBLE
//
//        // creating a new variable for our request queue
//        val queue = Volley.newRequestQueue(this@MainActivity)
//
//        // on below line we are calling a string
//        // request method to post the data to our API
//        // in this we are calling a post method.
//        val request: StringRequest =
//            object : StringRequest(Method.POST, url, Response.Listener { response ->
//                // inside on response method we are
//                // hiding our progress bar
//                // and setting data to edit text as empty
//                loadingPB!!.visibility = View.GONE
//                nameEdt!!.setText("")
//                jobEdt!!.setText("")
//
//                // on below line we are displaying a success toast message.
//                Toast.makeText(this@MainActivity, "Data added to API", Toast.LENGTH_SHORT).show()
//                try {
//                    // on below line we are parsing the response
//                    // to json object to extract data from it.
//                    val respObj = JSONObject(response)
//
//                    // below are the strings which we
//                    // extract from our json object.
//                    val name = respObj.getString("name")
//                    val job = respObj.getString("job")
//
//                    // on below line we are setting this string s to our text view.
//                    responseTV!!.text = "Name : $name\nJob : $job"
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }, Response.ErrorListener { error -> // method to handle errors.
//                Toast.makeText(
//                    this@MainActivity,
//                    "Fail to get response = $error",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }) {
//                override fun getParams(): Map<String, String>? {
//                    // below line we are creating a map for
//                    // storing our values in key and value pair.
//                    val params: MutableMap<String, String> = HashMap()
//
//                    // on below line we are passing our key
//                    // and value pair to our parameters.
//                    params["name"] = name
//                    params["job"] = job
//
//                    // at last we are
//                    // returning our params.
//                    return params
//                }
//            }
//        // below line is to make
//        // a json object request.
//        queue.add(request)
//    }
//}