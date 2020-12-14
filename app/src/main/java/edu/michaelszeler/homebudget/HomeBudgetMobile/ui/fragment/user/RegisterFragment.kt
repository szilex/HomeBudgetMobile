package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.user

import android.os.Bundle
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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.validation.TextInputValidator
import kotlinx.android.synthetic.main.fragment_register.view.*
import org.json.JSONObject

class RegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)

        view.button_register_sign_up.setOnClickListener {
            val login = view.edit_text_register_login.text.toString()
            val password = view.edit_text_register_password.text.toString()
            val passwordReEnter = view.edit_text_register_password_reenter.text.toString()
            val firstName = view.edit_text_register_first_name.text.toString()
            val lastName = view.edit_text_register_last_name.text.toString()
            val checked = view.checkbox_register_terms.isChecked

            if (TextInputValidator.isLoginValid(login) && TextInputValidator.isPasswordValid(password) && TextInputValidator.isPasswordValid(passwordReEnter) && TextInputValidator.isNameValid(firstName) && TextInputValidator.isNameValid(lastName) && checked) {
                if (password == passwordReEnter) {
                    val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

                    val jsonObjectRequest = JsonObjectRequest(
                        Request.Method.POST,
                        "http://10.0.2.2:8080/user",
                        JSONObject(String.format("{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\"}", login, password, firstName, lastName)),
                        {
                                response: JSONObject? ->
                            run {
                                Log.e("Rest Response", response.toString())
                                Toast.makeText(activity, "User created!", Toast.LENGTH_SHORT).show()
                                (activity as NavigationHost).navigateTo(LoginFragment(), false)
                            }
                        },
                        {
                                error: VolleyError? ->
                            run {
                                Log.e("Error rest response", error.toString())
                                val networkResponse : NetworkResponse? = error?.networkResponse
                                val jsonError : String? = networkResponse?.data?.let { it1 -> String(it1) }
                                val answer = JSONObject(jsonError ?: "{}")
                                if (answer.has("message")) {
                                    Log.e("Error rest data", answer.getString("message") ?: "empty")
                                    when (answer.getString("message")) {
                                        "login is already taken" -> {
                                            view.edit_text_register_login.error = "Username taken"
                                        }
                                        "insufficient argument list" -> {
                                            Toast.makeText(activity, "Server received insufficient argument list", Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                            Toast.makeText(activity, "Server error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(activity, "Unknown server error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                    requestQueue.add(jsonObjectRequest)
                } else {
                    view.edit_text_register_password_reenter.error = "Password does not match the original"
                }
            } else {
                if (TextInputValidator.isLoginValid(login)) {
                    view.edit_text_register_login.error = "Please enter valid login"
                }
                if (TextInputValidator.isPasswordValid(password)) {
                    view.edit_text_register_password.error = "Please enter valid password"
                }
                if (TextInputValidator.isPasswordValid(passwordReEnter)) {
                    if (passwordReEnter.isBlank()) {
                        view.edit_text_register_password_reenter.error = "Please re-enter password"
                    } else {
                        view.edit_text_register_password_reenter.error = "Please re-enter valid password"
                    }
                }
                if (TextInputValidator.isNameValid(firstName)) {
                    view.edit_text_register_first_name.error = "Please enter first name"
                }
                if (TextInputValidator.isNameValid(lastName)) {
                    view.edit_text_register_last_name.error = "Please enter last name"
                }
                if (!checked) {
                    view.checkbox_register_terms.error = "Please agree to the terms of use"
                }
            }
        }

        view.button_register_back_to_login.setOnClickListener {
            activity?.onBackPressed()
        }

        setUpTextChangedListener(view.edit_text_register_login)
        setUpTextChangedListener(view.edit_text_register_password)
        setUpTextChangedListener(view.edit_text_register_password_reenter)
        setUpTextChangedListener(view.edit_text_register_first_name)
        setUpTextChangedListener(view.edit_text_register_last_name)

        view.checkbox_register_terms.setOnClickListener {
            view.checkbox_register_terms.error = null
        }

        return view
    }

    private fun setUpTextChangedListener(view: EditText) {
        view.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { view.error = null }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }
}