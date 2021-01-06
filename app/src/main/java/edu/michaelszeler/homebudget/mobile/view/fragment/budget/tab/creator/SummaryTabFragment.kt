package edu.michaelszeler.homebudget.mobile.view.fragment.budget.tab.creator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.michaelszeler.homebudget.mobile.R
import edu.michaelszeler.homebudget.mobile.model.budget.BudgetEntry
import edu.michaelszeler.homebudget.mobile.model.expense.CustomExpenseEntry
import edu.michaelszeler.homebudget.mobile.model.expense.RegularExpenseEntry
import edu.michaelszeler.homebudget.mobile.model.strategy.StrategyEntry
import edu.michaelszeler.homebudget.mobile.session.SessionManager
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.mobile.view.fragment.MainMenuFragment
import kotlinx.android.synthetic.main.fragment_new_budget_summary_tab.view.*
import org.json.JSONObject
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class SummaryTabFragment() : Fragment() {

    private lateinit var sessionManager : SessionManager

    private var date: Date? = null
    private var income: BigDecimal? = null
    private var customExpenses: MutableList<CustomExpenseEntry> = mutableListOf()
    private var regularExpenses: MutableList<RegularExpenseEntry> = mutableListOf()
    private var strategies: MutableList<StrategyEntry> = mutableListOf()

    private var dateChanged = false
    private var incomeChanged = false
    private var customExpensesChanged = false
    private var regularExpensesChanged = false
    private var strategiesChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_budget_summary_tab, container, false)

        view.text_view_new_budget_summary_tab_date.text = if (date == null) "-" else getDateString()
        view.text_view_new_budget_summary_tab_income.text = if (income == null) "-" else String.format("%10.2f", income)
        view.text_view_new_budget_summary_tab_custom_expenses.text = if (customExpenses.isEmpty()) "-" else String.format("%10.2f", customExpenses.stream().map { x -> x.amount }.reduce(BigDecimal.ZERO, BigDecimal::add))
        view.text_view_new_budget_summary_tab_regular_expenses.text = if (regularExpenses.isEmpty()) "-" else String.format("%10.2f", regularExpenses.stream().map { x -> x.amount }.reduce(BigDecimal.ZERO, BigDecimal::add))
        view.text_view_new_budget_summary_tab_strategies.text = if (strategies.isEmpty()) "-" else String.format("%10.2f", strategies.stream().map { x -> x.goal }.reduce(BigDecimal.ZERO, BigDecimal::add))

        view.button_new_budget_summary_tab_create.setOnClickListener {
            if (isArgumentListSet()) {

                val token = sessionManager.getToken()!!
                val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

                val budget = BudgetEntry(0, date!!, income!!, customExpenses, regularExpenses, strategies)

                val jsonContent = convertToJsonString(budget)

                val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,
                        "http://10.0.2.2:8080/budget",
                        JSONObject(jsonContent),
                        {
                            response: JSONObject? ->
                            run {
                                Log.e("Rest Response", response.toString())
                                Toast.makeText(activity, "Budget created!", Toast.LENGTH_SHORT).show()
                                (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                            }
                        },
                        {
                            error: VolleyError? ->
                            run {
                                Log.e("Error rest response", error.toString())
                                val networkResponse : NetworkResponse? = error?.networkResponse
                                if (networkResponse?.statusCode == 401) {
                                    Toast.makeText(activity, "Authentication error", Toast.LENGTH_SHORT).show()
                                    //(activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                                } else {
                                    val responseData = String(networkResponse?.data ?: "{}".toByteArray())
                                    val answer = JSONObject(if (responseData.isBlank()) "{}" else responseData)
                                    if (answer.has("message")) {
                                        Log.e("Error rest data", answer.getString("message") ?: "empty")
                                        when (answer.getString("message")) {
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
                        }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = token
                        headers["Content-Type"] = "application/json"
                        return headers
                    }
                }
                requestQueue.add(jsonObjectRequest)
            } else {
                Toast.makeText(activity, "Incomplete budget info", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (dateChanged) {
            view?.text_view_new_budget_summary_tab_date?.text = if (date == null) "-" else getDateString()
            dateChanged = false
        }
        if (incomeChanged) {
            view?.text_view_new_budget_summary_tab_income?.text = if (income == null) "-" else String.format("%10.2f", income)
            incomeChanged = false
        }
        if (customExpensesChanged) {
            view?.text_view_new_budget_summary_tab_custom_expenses?.text = if (customExpenses.isEmpty()) "-" else String.format("%10.2f", customExpenses.stream().map { x -> x.amount }.reduce(BigDecimal.ZERO, BigDecimal::add))
            customExpensesChanged = false
        }
        if (regularExpensesChanged) {
            view?.text_view_new_budget_summary_tab_regular_expenses?.text = if (regularExpenses.isEmpty()) "-" else String.format("%10.2f", regularExpenses.stream().map { x -> x.amount }.reduce(BigDecimal.ZERO, BigDecimal::add))
            regularExpensesChanged = false
        }
        if (strategiesChanged) {
            view?.text_view_new_budget_summary_tab_strategies?.text = if (strategies.isEmpty()) "-" else String.format("%10.2f", strategies.stream().map { x -> x.goal }.reduce(BigDecimal.ZERO, BigDecimal::add))
            strategiesChanged = false
        }

    }

    private fun getDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return String.format("%d-%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
    }

    fun setDate(date: Date?) {
        this.date = date
        dateChanged = true
    }

    fun setIncome(income: BigDecimal?) {
        this.income = income
        incomeChanged = true
    }

    fun setCustomExpenses(customExpenses: MutableList<CustomExpenseEntry>) {
        this.customExpenses = customExpenses
        customExpensesChanged = true
    }

    fun setRegularExpenses(regularExpenses: MutableList<RegularExpenseEntry>) {
        this.regularExpenses = regularExpenses
        regularExpensesChanged = true
    }

    fun setStrategies(strategies: MutableList<StrategyEntry>) {
        this.strategies = strategies
        strategiesChanged = true
    }

    private fun convertToJsonString(budget: BudgetEntry) : String {

        val stringBuilder = StringBuilder()
        stringBuilder.append(String.format("{\"income\":%10.2f,", budget.income))
        stringBuilder.append("\"date\": \"").append(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(budget.date)).append("\",")
        stringBuilder.append("\"customExpenses\": [")

        if (customExpenses != null) {
            val customExpenseIterator = budget.customExpenses.iterator()
            while (customExpenseIterator.hasNext()) {
                val expense = customExpenseIterator.next()
                stringBuilder.append("{\"category\": \"").append(expense.category).append("\",")
                stringBuilder.append("\"name\": \"").append(expense.name).append("\",")
                stringBuilder.append(String.format("\"amount\": %10.2f,", expense.amount))
                stringBuilder.append("\"date\": \"").append(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(expense.date)).append("\",")
                stringBuilder.append("\"months\": 1}")
                if (customExpenseIterator.hasNext()) {
                    stringBuilder.append(",")
                }
            }
        }
        stringBuilder.append("],\"regularExpenses\":[")

        if (regularExpenses != null) {
            val regularExpenseIterator = budget.regularExpenses.iterator()
            while (regularExpenseIterator.hasNext()) {
                val expense = regularExpenseIterator.next()
                stringBuilder.append(String.format("{\"id\": %d}", expense.id))
                if (regularExpenseIterator.hasNext()) {
                    stringBuilder.append(",")
                }
            }
        }
        stringBuilder.append("],\"strategies\":[")

        if (strategies != null) {
            val strategyIterator = budget.strategies.iterator()
            while (strategyIterator.hasNext()) {
                val strategy = strategyIterator.next()
                stringBuilder.append(String.format("{\"id\": %d}", strategy.id))
                if (strategyIterator.hasNext()) {
                    stringBuilder.append(",")
                }
            }
        }
        stringBuilder.append("]}")

        return stringBuilder.toString()
    }

    private fun isArgumentListSet() : Boolean {
        return date != null && income != null && customExpenses != null && regularExpenses != null && strategies != null
    }
}