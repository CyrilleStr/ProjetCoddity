package com.sosacy.projetcoddity.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.sosacy.projetcoddity.databinding.FragmentLoginBinding
import com.sosacy.projetcoddity.web.WebClient

import com.sosacy.projetcoddity.R
import com.sosacy.projetcoddity.data.Result
import org.json.JSONObject

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null
    private val SHARED_PREF_USER = "SHARED_PREF_USER"
    private val SHARED_PREF_USER_TOKEN = "SHARED_PREF_USER_TOKEN"
    private val SHARED_PREF_USER_ID = "SHARED_PREF_USER_ID"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        /* Get authentication Token */
//        val token = this.activity?.getSharedPreferences(SHARED_PREF_USER, AppCompatActivity.MODE_PRIVATE)
//            ?.getString(SHARED_PREF_USER_TOKEN, null)
//        Log.d("token",token.toString())
//        if (token != null) {
//            Log.d("token",token)
//            /* Start activities */
//            startActivity(Intent(this, HomeActivity::class.java))
//        }

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
//                loginViewModel.loginDataChanged(
//                    usernameEditText.text.toString(),
//                    passwordEditText.text.toString()
//                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                loginViewModel.login(
//                    usernameEditText.text.toString(),
//                    passwordEditText.text.toString()
//                )
                login(usernameEditText.text.toString(), passwordEditText.text.toString())
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
//            loginViewModel.login(
//                usernameEditText.text.toString(),
//                passwordEditText.text.toString()
//            )
            login(usernameEditText.text.toString(), passwordEditText.text.toString())
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + " " + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
        Navigation.findNavController(this.requireView()).navigate(R.id.action_loginFragment_to_navigation_home)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun login(username:String, password:String){
        val credentials = JSONObject()
        credentials.put("username", username)
        credentials.put("password", password)
        WebClient(this.requireActivity().applicationContext).authenticate(credentials,
            {
            response ->
                /* Get reponse data*/
                Log.d("response",response.toString())
                val jsonObj: JSONObject = JSONObject(response.toString())
                var token = jsonObj.get("token").toString()
                var userId = jsonObj.get("user_id").toString()

                /* Store user information */
                val preferences = this.requireActivity().getSharedPreferences(SHARED_PREF_USER,
                    AppCompatActivity.MODE_PRIVATE
                )
                val editor = preferences.edit()
                editor.putString(SHARED_PREF_USER_TOKEN, token)
                editor.putString(SHARED_PREF_USER_ID, userId)
                editor.apply()

                /* Login succeed (1 second delay) */
                Handler(Looper.getMainLooper()).postDelayed({
                    loginViewModel.loginSucceed(username)
                }, 1000)

            },
            {
                loginViewModel.loginFailed()
            }
        )
    }
}