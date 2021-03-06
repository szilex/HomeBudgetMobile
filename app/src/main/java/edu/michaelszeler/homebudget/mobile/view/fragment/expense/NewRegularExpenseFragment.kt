package edu.michaelszeler.homebudget.mobile.view.fragment.expense

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.michaelszeler.homebudget.mobile.R
import edu.michaelszeler.homebudget.mobile.session.SessionManager
import edu.michaelszeler.homebudget.mobile.view.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.mobile.utils.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.mobile.utils.validation.TextInputValidator
import kotlinx.android.synthetic.main.fragment_new_expense.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class NewRegularExpenseFragment : Fragment() {
    private lateinit var sessionManager : SessionManager
    private lateinit var categories : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_new_expense, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_new_expense)

        view.toolbar_new_expense.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!,
                view.nested_scroll_view_new_expense,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon)
            )
        )

        FragmentNavigationUtility.setUpMenuButtons((activity as NavigationHost), view)

        val token = sessionManager.getToken()!!
        val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

        val jsonArrayRequest = object: JsonArrayRequest(
            Method.GET,
            "http://10.0.2.2:8080/expense/categories",
            null,
            { response: JSONArray? ->
                run {
                    Log.e("Rest Response", response.toString())
                    val length = response?.length()?.minus(1) ?: 0
                    categories = Array(length.plus(1)) { "" }
                    for (i in 0..length) {
                        val jsonObject = response?.get(i) as? JSONObject?
                        categories[i] = jsonObject?.getString("name") ?: ""
                    }
                    val spinner = view.findViewById<AutoCompleteTextView>(R.id.spinner_new_expense_category)
                    val adapter = ArrayAdapter(activity as Context, R.layout.dropdown_simple_item, categories)
                    spinner.setAdapter(adapter)
                    spinner.setOnClickListener {
                        spinner.showDropDown()
                    }
                }
            },
            { error: VolleyError? ->
                run {
                    Log.e("Error rest response", error.toString())
                    Toast.makeText(activity,"Could not obtain categories from server",  Toast.LENGTH_SHORT).show()
                    (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }

        requestQueue.add(jsonArrayRequest)

        val calendar = Calendar.getInstance()
        val editText = view.findViewById(R.id.edit_text_new_expense_start_date) as EditText
        val date = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
                editText.setText(sdf.format(calendar.time))
        }

        editText.setOnClickListener{
            DatePickerDialog(activity as AppCompatActivity, R.style.DialogTheme, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        view.button_new_expense_enter.setOnClickListener {

            val name = view.edit_text_new_expense_name.text.toString()
            val category = view.spinner_new_expense_category.text.toString()
            val amount = view.edit_text_new_expense_amount.text.toString()
            val startDate = view.edit_text_new_expense_start_date.text.toString()
            val months = view.edit_text_new_expense_months.text.toString()

            if (TextInputValidator.isExpenseNameValid(name) && category.isNotBlank() && TextInputValidator.isAmountValid(amount) && TextInputValidator.isDateValid(startDate) && TextInputValidator.isMonthAmountValid(months)) {

                Log.e("Message sent",String.format("{\"name\":\"%s\",\"category\":\"%s\",\"amount\":\"%s\",\"startDate\":\"%s\",\"months\":\"%s\"}", name, category, amount, startDate, months))

                val jsonArrayRequest2 = object : JsonObjectRequest(
                    Method.POST,
                    "http://10.0.2.2:8080/expense",
                    JSONObject(String.format("{\"name\":\"%s\",\"category\":\"%s\",\"amount\":\"%s\",\"startDate\":\"%s\",\"months\":\"%s\"}", name, category, amount, startDate, months)),
                    { response: JSONObject? ->
                        run {
                            Log.e("Rest Response", response.toString())
                            Toast.makeText(activity, "Expense created!", Toast.LENGTH_SHORT).show()
                            (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                        }
                    },
                    { error: VolleyError? ->
                        run {
                            Log.e("Error rest response", error.toString())
                            val networkResponse: NetworkResponse? = error?.networkResponse
                            if (networkResponse?.statusCode == 401) {
                                Toast.makeText(activity, "Authentication error", Toast.LENGTH_SHORT).show()
                                (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                            } else {
                                val responseData = String(networkResponse?.data ?: "{}".toByteArray())
                                val answer = JSONObject(if (responseData.isBlank()) "{}" else responseData)
                                if (answer.has("message")) {
                                    Log.e("Error rest data", answer.getString("message") ?: "empty")
                                    when (answer.getString("message")) {
                                        "insufficient argument list" -> {
                                            Toast.makeText(activity,"Server received insufficient argument list",Toast.LENGTH_SHORT).show()
                                        }
                                        "category not found" -> {
                                            Toast.makeText(activity,"Category could not be found", Toast.LENGTH_SHORT).show()
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
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = token
                        headers["Content-Type"] = "application/json"
                        return headers
                    }
                }
                requestQueue.add(jsonArrayRequest2)
            } else {
                if (!TextInputValidator.isExpenseNameValid(name)) {
                    view.edit_text_new_expense_name.error = "Please enter valid name"
                }
                if (view.spinner_new_expense_category.text.isBlank()) {
                    view.spinner_new_expense_category.error = "Please choose category"
                }
                if (!TextInputValidator.isAmountValid(amount)) {
                    view.edit_text_new_expense_amount.error = "Please enter valid amount"
                }
                if (!TextInputValidator.isDateValid(startDate)) {
                    view.edit_text_new_expense_start_date.error = "Please enter valid date"
                }
                if (!TextInputValidator.isMonthAmountValid(months)) {
                    view.edit_text_new_expense_months.error = "Please enter valid amount of months"
                }
            }
        }

        setUpTextChangedListener(view.edit_text_new_expense_name)
        setUpTextChangedListener(view.spinner_new_expense_category)
        setUpTextChangedListener(view.edit_text_new_expense_amount)
        setUpTextChangedListener(view.edit_text_new_expense_start_date)
        setUpTextChangedListener(view.edit_text_new_expense_months)

        return view
    }

    private fun setUpTextChangedListener(editText: EditText) {
        editText.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { editText.error = null }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_back, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_go_back -> {
                (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}