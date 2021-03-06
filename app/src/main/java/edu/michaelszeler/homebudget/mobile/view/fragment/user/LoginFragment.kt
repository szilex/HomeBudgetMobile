package edu.michaelszeler.homebudget.mobile.view.fragment.user

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import edu.michaelszeler.homebudget.mobile.R
import edu.michaelszeler.homebudget.mobile.session.SessionManager
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.mobile.utils.validation.TextInputValidator
import edu.michaelszeler.homebudget.mobile.view.fragment.MainMenuFragment
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.json.JSONObject

class LoginFragment : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sessionManager = SessionManager(activity)
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val handler = Handler()
        val runnable = Runnable {
            view.relative_layout_login.visibility = View.VISIBLE
            view.relative_layout_login_2.visibility = View.VISIBLE
        }

        view.button_login_enter.setOnClickListener {
            val login = view.edit_text_login_login.text.toString()
            val password = view.edit_text_login_password.text.toString()

            if (TextInputValidator.isLoginValid(login) && TextInputValidator.isPasswordValid(password)) {

                val requestQueue : RequestQueue = Volley.newRequestQueue(activity)
                var token = ""

                val jsonObjectRequest = object: JsonObjectRequest(
                        Method.POST,
                        "http://10.0.2.2:8080/login",
                        JSONObject(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password)),
                        { response: JSONObject? ->
                            run {
                                Log.e("Rest Response", response.toString())
                                sessionManager.createSession(login, password, token)
                                (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                            }
                        },
                        { error: VolleyError? ->
                            run {
                                Log.e("Error rest response", error.toString())
                                val networkResponse: NetworkResponse? = error?.networkResponse

                                if (networkResponse?.statusCode == 401) {
                                    Toast.makeText(activity, "Authentication error", Toast.LENGTH_SHORT).show()
                                    (activity as NavigationHost).navigateTo(LoginFragment(), false)
                                } else {
                                    val responseData = String(networkResponse?.data ?: "{}".toByteArray())
                                    val answer = JSONObject(if (responseData.isBlank()) "{}" else responseData)
                                    if (answer.has("message")) {
                                        Log.e("Error rest data", answer.getString("message") ?: "empty")
                                        when (answer.getString("message")) {
                                            "incorrect username" -> {
                                                view.text_input_layout_login_login.error =
                                                        "Incorrect login"
                                            }
                                            "incorrect password" -> {
                                                view.text_input_layout_login_password.error =
                                                        "Incorrect password"
                                            }
                                            "Bad credentials" -> {
                                                Toast.makeText(activity, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                                            }
                                            else -> {
                                                Toast.makeText(activity, "Server error", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(activity, "Unknown server error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        return headers
                    }

                    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                        val headers = response?.headers
                        val newResponse : NetworkResponse
                        token = headers?.get("Authorization")!!
                        newResponse = if (response.data.isEmpty()) {
                            val responseData = "{}".toByteArray()
                            NetworkResponse(response.statusCode, responseData, response.notModified, response.networkTimeMs, response.allHeaders)
                        } else {
                            response
                        }
                        return super.parseNetworkResponse(newResponse)
                    }
                }

                requestQueue.add(jsonObjectRequest)

            } else {
                if (!TextInputValidator.isLoginValid(login)) {
                    view.text_input_layout_login_login.error = "Please enter valid login"
                }
                if (!TextInputValidator.isPasswordValid(password)) {
                    view.text_input_layout_login_password.error = "Please enter valid password"
                }
            }
        }

        setUpTextChangedListener(view.edit_text_login_login, view.text_input_layout_login_login)
        setUpTextChangedListener(view.edit_text_login_password, view.text_input_layout_login_password)

        view.button_login_register.setOnClickListener {
            (activity as NavigationHost).navigateTo(RegisterFragment(), true)
        }

        view.button_login_forgot_password.setOnClickListener {
            (activity as NavigationHost).navigateTo(ChangePasswordFragment(), true)
        }

        handler.postDelayed(runnable, 3000)

        return view
    }

    private fun setUpTextChangedListener(editText: EditText, textInputLayout: TextInputLayout) {
        editText.addTextChangedListener(object :
                TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                textInputLayout.error = null
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }
}