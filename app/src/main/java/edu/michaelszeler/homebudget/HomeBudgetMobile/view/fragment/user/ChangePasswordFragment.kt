package edu.michaelszeler.homebudget.HomeBudgetMobile.view.fragment.user

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
import kotlinx.android.synthetic.main.fragment_recover_password.view.*
import kotlinx.android.synthetic.main.fragment_register.view.*
import org.json.JSONObject

class ChangePasswordFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_recover_password, container, false)

        view.button_recovery_change_password.setOnClickListener {
            val login = view.edit_text_change_password_login.text.toString()
            val password = view.edit_text_change_password_password.text.toString()
            val passwordReEnter = view.edit_text_change_password_password_reenter.text.toString()

            if (TextInputValidator.isLoginValid(login) && TextInputValidator.isPasswordValid(password) && TextInputValidator.isPasswordValid(passwordReEnter)) {
                if (password == passwordReEnter) {
                    val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

                    val jsonObjectRequest = JsonObjectRequest(
                        Request.Method.PUT,
                        "http://10.0.2.2:8080/user/changePassword",
                        JSONObject(String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password)),
                        {
                                response: JSONObject? ->
                            run {
                                Log.e("Rest Response", response.toString())
                                Toast.makeText(activity, "Password changed!", Toast.LENGTH_SHORT).show()
                                (activity as NavigationHost).navigateTo(LoginFragment(), false)
                            }
                        },
                        {
                                error: VolleyError? ->
                            run {
                                Log.e("Error rest response", error.toString())
                                val networkResponse : NetworkResponse? = error?.networkResponse
                                val responseData = String(networkResponse?.data ?: "{}".toByteArray())
                                val answer = JSONObject(if (responseData.isBlank()) "{}" else responseData)
                                if (answer.has("message")) {
                                    Log.e("Error rest data", answer.getString("message") ?: "empty")
                                    when (answer.getString("message")) {
                                        "user not found" -> {
                                            view.edit_text_change_password_login.error = "Incorrect login"
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
                    view.edit_text_change_password_login.error = "Please enter valid login"
                }
                if (TextInputValidator.isPasswordValid(password)) {
                    view.edit_text_change_password_password.error = "Please enter valid password"
                }
                if (TextInputValidator.isPasswordValid(passwordReEnter)) {
                    if (passwordReEnter.isBlank()) {
                        view.edit_text_change_password_password_reenter.error = "Please re-enter password"
                    } else {
                        view.edit_text_change_password_password_reenter.error = "Please re-enter valid password"
                    }
                }
            }
        }

        view.button_recovery_back_to_login.setOnClickListener {
            activity?.onBackPressed()
        }

        setUpTextChangedListener(view.edit_text_change_password_login)
        setUpTextChangedListener(view.edit_text_change_password_password)
        setUpTextChangedListener(view.edit_text_change_password_password_reenter)

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