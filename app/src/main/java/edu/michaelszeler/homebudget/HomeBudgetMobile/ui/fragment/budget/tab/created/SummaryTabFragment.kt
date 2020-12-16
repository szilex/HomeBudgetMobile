package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.budget.tab.created

import android.os.Bundle
import android.util.Base64
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
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.budget.BudgetEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.CustomExpenseEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.RegularExpenseEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.strategy.StrategyEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationHost
import kotlinx.android.synthetic.main.fragment_new_budget_summary_tab.view.*
import org.json.JSONObject
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class SummaryTabFragment() : Fragment() {

    private lateinit var sessionManager : SessionManager

    private lateinit var date: Date
    private lateinit var income: BigDecimal
    private lateinit var customExpenses: MutableList<CustomExpenseEntry>
    private lateinit var regularExpenses: MutableList<RegularExpenseEntry>
    private lateinit var strategies: MutableList<StrategyEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_budget_summary_tab, container, false)
        view.button_new_budget_summary_tab_create.setOnClickListener {
            if (isArgumentListSet()) {

                val login = sessionManager.getUserDetails()?.get("login")
                val password = sessionManager.getUserDetails()?.get("password")
                val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

                val budget = BudgetEntry(0, date, income, customExpenses, regularExpenses, strategies)

                val stringBuilder = StringBuilder()
                stringBuilder.append(String.format("{\"income\":%10.2f,", budget.income))
                stringBuilder.append("\"date\": \"").append(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(budget.date)).append("\",")
                stringBuilder.append("\"customExpenses\": [")

                val customExpenseIterator = customExpenses.iterator()
                while (customExpenseIterator.hasNext()) {
                    val expense = customExpenseIterator.next()
                    stringBuilder.append("{\"category\": \"").append(expense.category).append("\",")
                    stringBuilder.append("\"name\": \"").append(expense.name).append("\",")
                    stringBuilder.append(String.format("{\"amount\": %f},", DecimalFormat("#.##").format(expense.amount)))
                    stringBuilder.append("\"date\": ").append(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(expense.date)).append("\",")
                    stringBuilder.append("\"months\": 1}")
                    if (customExpenseIterator.hasNext()) {
                        stringBuilder.append(",")
                    }
                }
                stringBuilder.append("],\"regularExpenses\":[")

                val regularExpenseIterator = regularExpenses.iterator()
                while (regularExpenseIterator.hasNext()) {
                    val expense = regularExpenseIterator.next()
                    stringBuilder.append(String.format("{\"id\": %d}", expense.id))
                    /*stringBuilder.append("{\"category\": \"").append(expense.category).append("\",")
                    stringBuilder.append("\"name\": \"").append(expense.name).append("\",")
                    stringBuilder.append(String.format("{\"amount\": %f},", DecimalFormat("#.##").format(expense.amount)))
                    stringBuilder.append("\"startDate\": ").append(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(expense.startDate)).append("\",")
                    stringBuilder.append(String.format("\"months\": %d", expense.months))*/
                    if (regularExpenseIterator.hasNext()) {
                        stringBuilder.append(",")
                    }
                }
                stringBuilder.append("],\"strategies\":[")

                val strategyIterator = strategies.iterator()
                while (strategyIterator.hasNext()) {
                    val strategy = strategyIterator.next()
                    stringBuilder.append(String.format("{\"id\": %d}", strategy.id))
                    /*stringBuilder.append("{\"category\": \"").append(strategy.category).append("\",")
                    stringBuilder.append("\"name\": \"").append(strategy.name).append("\",")
                    stringBuilder.append("\"name\": \"").append(strategy.description).append("\",")
                    stringBuilder.append(String.format("{\"goal\": %f},", DecimalFormat("#.##").format(strategy.goal)))
                    stringBuilder.append("\"startDate\": ").append(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(strategy.startDate)).append("\",")
                    stringBuilder.append(String.format("\"months\": %d", strategy.months))*/
                    if (strategyIterator.hasNext()) {
                        stringBuilder.append(",")
                    }
                }
                stringBuilder.append("]}")

                val answer = stringBuilder.toString()

                val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,
                        "http://10.0.2.2:8080/budget",
                        JSONObject(stringBuilder.toString()),
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
                                val jsonError : String? = String(networkResponse?.data!!)
                                val answer = JSONObject(jsonError ?: "{}")
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
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = String.format("Basic %s", Base64.encodeToString(String.format("%s:%s", login, password).toByteArray(), Base64.NO_WRAP))
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

    fun setDate(date: Date) {
        this.date = date
    }

    fun setIncome(income: BigDecimal) {
        this.income = income
    }

    fun setCustomExpenses(customExpenses: MutableList<CustomExpenseEntry>) {
        this.customExpenses = customExpenses
    }

    fun setRegularExpenses(regularExpenses: MutableList<RegularExpenseEntry>) {
        this.regularExpenses = regularExpenses
    }

    fun setStrategies(strategies: MutableList<StrategyEntry>) {
        this.strategies = strategies
    }

    private fun isArgumentListSet() : Boolean {
        return this::date.isInitialized && this::income.isInitialized && this::customExpenses.isInitialized && this::regularExpenses.isInitialized && this::strategies.isInitialized
    }
}