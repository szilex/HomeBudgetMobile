package edu.michaelszeler.homebudget.mobile.view.fragment.budget

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.michaelszeler.homebudget.mobile.R
import edu.michaelszeler.homebudget.mobile.model.expense.CustomExpenseEntry
import edu.michaelszeler.homebudget.mobile.model.expense.RegularExpenseEntry
import edu.michaelszeler.homebudget.mobile.model.strategy.StrategyEntry
import edu.michaelszeler.homebudget.mobile.session.SessionManager
import edu.michaelszeler.homebudget.mobile.utils.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.mobile.view.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.mobile.view.fragment.budget.tab.creator.*
import kotlinx.android.synthetic.main.fragment_new_budget.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class NewBudgetFragment : Fragment(), FragmentCallback {

    private lateinit var sessionManager : SessionManager

    private lateinit var pagerAdapter: FragmentStateAdapter
    private val titles = arrayOf("General", "Custom expense", "Regular expense", "Strategy", "Summary")

    @Volatile private  var date: Date = Date(0)
    @Volatile private  var income: BigDecimal = BigDecimal.ZERO
    @Volatile private lateinit var expenseCategories: List<String>
    @Volatile private var customExpenses: MutableList<CustomExpenseEntry> = mutableListOf()
    @Volatile private var regularExpenses: MutableList<RegularExpenseEntry> = mutableListOf()
    @Volatile private var strategies: MutableList<StrategyEntry> = mutableListOf()

    private var isExpenseCategoriesInitialized = false
    private var isRegularExpensesInitialized = false
    private var isStrategiesInitialized = false

    private var requestError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_new_budget, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_new_budget)

        val token = sessionManager.getToken()!!
        val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

        val expenseCategoriesJsonArrayRequest = object: JsonArrayRequest(
                Method.GET,
                "http://10.0.2.2:8080/expense/categories",
                null,
                { response: JSONArray? ->
                    run {
                        Log.e("Rest Response", response.toString())
                        val length = response?.length()?.minus(1) ?: 0
                        val categories = Array(length.plus(1)) { "" }
                        for (i in 0..length) {
                            val jsonObject = response?.get(i) as? JSONObject?
                            categories[i] = jsonObject?.getString("name") ?: ""
                        }
                        expenseCategories = categories.toList()
                        isExpenseCategoriesInitialized = true
                    }
                },
                { error: VolleyError? ->
                    run {
                        Log.e("Error rest response", error.toString())
                        requestError = true
                        Toast.makeText(activity,"Could not obtain expense categories from server",  Toast.LENGTH_SHORT).show()
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

        val regularExpensesJsonArrayRequest = object: JsonArrayRequest(
                Method.GET,
                "http://10.0.2.2:8080/expense",
                null,
                {
                    response: JSONArray? ->
                    run {
                        Log.e("Rest Response", response.toString())
                        val regularExpenseList = RegularExpenseEntry.convertToRegularExpenseList(response.toString()).toMutableList()
                        if (regularExpenseList.isNotEmpty()) regularExpenses = regularExpenseList.toMutableList()
                        isRegularExpensesInitialized = true
                    }
                },
                {
                    error: VolleyError? ->
                    run {
                        Log.e("Error rest response", error.toString())
                        requestError = true
                        val networkResponse : NetworkResponse? = error?.networkResponse
                        if (networkResponse?.statusCode == 401) {
                            Toast.makeText(activity, "Authentication error", Toast.LENGTH_SHORT).show()
                            (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                        } else {
                            val responseData = String(networkResponse?.data ?: "{}".toByteArray())
                            val answer = JSONObject(if (responseData.isBlank()) "{}" else responseData)
                            if (answer.has("message")) {
                                Log.e("Error rest data", answer.getString("message") ?: "")
                                when (answer.getString("message")) {
                                    "user not found" -> {
                                        Toast.makeText(activity, "Could not find user", Toast.LENGTH_SHORT).show()
                                    }
                                    "no regular expenses found" -> {
                                        Toast.makeText(activity, "Could not find regular expenses", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        Toast.makeText(activity, "Server error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(activity, "Unknown server error", Toast.LENGTH_SHORT).show()
                            }
                            (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                        }
                    }
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }

        val strategiesJsonArrayRequest = object: JsonArrayRequest(
                Method.GET,
                "http://10.0.2.2:8080/strategy",
                null,
                {
                    response: JSONArray? ->
                    run {
                        Log.e("Rest Response", response.toString())
                        val strategyList = StrategyEntry.convertToStrategyList(response.toString()).toMutableList()
                        if (strategyList.isNotEmpty())  strategies =  strategyList.toMutableList()
                        isStrategiesInitialized = true
                    }
                },
                {
                    error: VolleyError? ->
                    run {
                        Log.e("Error rest response", error.toString())
                        requestError = true
                        val networkResponse : NetworkResponse? = error?.networkResponse
                        if (networkResponse?.statusCode == 401) {
                            Toast.makeText(activity, "Authentication error", Toast.LENGTH_SHORT).show()
                            (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                        } else {
                            val responseData = String(networkResponse?.data ?: "{}".toByteArray())
                            val answer = JSONObject(if (responseData.isBlank()) "{}" else responseData)
                            if (answer.has("message")) {
                                Log.e("Error rest data", answer.getString("message") ?: "")
                                when (answer.getString("message")) {
                                    "user not found" -> {
                                        Toast.makeText(activity, "Could not find user", Toast.LENGTH_SHORT).show()
                                    }
                                    "no strategies found" -> {
                                        Toast.makeText(activity, "Could not find any current strategies", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        Toast.makeText(activity, "Server error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(activity, "Unknown server error", Toast.LENGTH_SHORT).show()
                            }
                            (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                        }
                    }
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }

        requestQueue.add(expenseCategoriesJsonArrayRequest)
        requestQueue.add(regularExpensesJsonArrayRequest)
        requestQueue.add(strategiesJsonArrayRequest)

        val handler = Handler()
        handler.postDelayed( {
            while (!requestError) {
                if (isExpenseCategoriesInitialized && isRegularExpensesInitialized && isStrategiesInitialized) {
/*
                    summaryTab = SummaryTabFragment()
                    summaryTab.setCustomExpenses(customExpenses)
                    summaryTab.setRegularExpenses(regularExpenses)
                    summaryTab.setStrategies(strategies)*/
                    break
                }
                Thread.sleep(100)
            }
            viewPager = view.findViewById(R.id.view_pager_new_budget)
            pagerAdapter = BudgetPagerAdapter(activity)
            viewPager!!.adapter = pagerAdapter
            val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout_new_budget)
            TabLayoutMediator(tabLayout, viewPager!!) { tab: TabLayout.Tab, position: Int -> tab.text = titles[position] }.attach()
            view.findViewById<RelativeLayout>(R.id.loading_panel_new_budget).visibility = View.GONE
        }, 1000)

        view.toolbar_new_budget.setNavigationOnClickListener(NavigationIconClickListener(activity!!, view.relative_layout_new_budget, AccelerateDecelerateInterpolator(), ContextCompat.getDrawable(context!!, R.drawable.menu_icon), ContextCompat.getDrawable(context!!, R.drawable.menu_icon)))
        FragmentNavigationUtility.setUpMenuButtons((activity as NavigationHost), view)

        return view
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

    @SuppressLint("SimpleDateFormat")
    override fun callback(args: Bundle) {
        if (args.containsKey("tab")) {
            when (args.get("tab")) {
                "general" -> {
                    val position = ParsePosition(0)
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    date = simpleDateFormat.parse(args.getString("date")!!, position)!!
                    income = BigDecimal(args.getString("amount"))
                    val summaryFragmentList: List<Fragment>? = fragmentManager?.fragments?.filter { fragment -> fragment.toString().contains("SummaryTabFragment") }
                    if (summaryFragmentList?.size == 1) {
                        val summaryFragment = summaryFragmentList[0]
                        (summaryFragment as SummaryTabFragment).setDate(date)
                        summaryFragment.setIncome(income)
                    }
                }
                "custom expenses" -> {
                    val position = ParsePosition(0)
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val expenseDate =  simpleDateFormat.parse(args.getString("date")!!, position)!!
                    val expense = CustomExpenseEntry(args.getString("name")!!, args.getString("category")!!, BigDecimal(args.getString("amount")), expenseDate)
                    customExpenses.add(0, expense)
                    val summaryFragmentList: List<Fragment>? = fragmentManager?.fragments?.filter { fragment -> fragment.toString().contains("SummaryTabFragment") }
                    if (summaryFragmentList?.size == 1) {
                        val summaryFragment = summaryFragmentList[0]
                        (summaryFragment as SummaryTabFragment).setCustomExpenses(customExpenses)
                    }
                }
            }
        }
    }

    private inner class BudgetPagerAdapter(activity: FragmentActivity?) : FragmentStateAdapter(activity!!){

        override fun createFragment(pos: Int): Fragment {
            return when (pos) {
                0 -> {
                    val fragment = GeneralTabFragment()
                    fragment.setFragmentCallback(this@NewBudgetFragment as FragmentCallback)
                    return fragment
                }
                1 -> {
                    val fragment = CustomExpensesTabFragment(expenseCategories, customExpenses)
                    fragment.setFragmentCallback(this@NewBudgetFragment as FragmentCallback)
                    return fragment
                }
                2 -> {
                    RegularExpensesTabFragment(regularExpenses)
                }
                3 -> {
                    StrategiesTabFragment(strategies)
                }
                4 -> {
                    SummaryTabFragment(date, income, customExpenses, regularExpenses, strategies)
                }
                else -> Fragment()
            }
        }

        override fun getItemCount(): Int {
            return PAGES
        }

    }

    companion object {
        private const val PAGES = 5

        var viewPager: ViewPager2? = null
    }
}