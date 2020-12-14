package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.budget

import android.os.Bundle
import android.os.Handler
import android.util.Base64
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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.budget.BudgetEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.budget.tab.CustomExpensesTabFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.budget.tab.GeneralTabFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.budget.tab.RegularExpensesTabFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.budget.tab.StrategiesTabFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationIconClickListener
import kotlinx.android.synthetic.main.fragment_current_budget.view.*
import org.json.JSONObject


class CurrentBudgetFragment : Fragment() {

    private lateinit var sessionManager : SessionManager
    private lateinit var budget : BudgetEntry

    private lateinit var pagerAdapter: FragmentStateAdapter
    private val titles = arrayOf("General", "Custom expense", "Regular expense", "Strategy")

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

        val view = inflater.inflate(R.layout.fragment_current_budget, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_current_budget)

        val login = sessionManager.getUserDetails()?.get("login")
        val password = sessionManager.getUserDetails()?.get("password")
        val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

        val jsonObjectRequest = object: JsonObjectRequest(
            Method.GET,
            "http://10.0.2.2:8080/budget",
            null,
            { response: JSONObject? ->
                run {
                    Log.e("Rest Response", response.toString())

                    budget = BudgetEntry.convertToBudget(response.toString())

                    viewPager = view.findViewById(R.id.view_pager_current_budget)
                    pagerAdapter = BudgetPagerAdapter(activity)
                    viewPager!!.adapter = pagerAdapter
                    val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout_current_budget)
                    TabLayoutMediator(
                        tabLayout,
                        viewPager!!
                    ) { tab: TabLayout.Tab, position: Int -> tab.text = titles[position] }.attach()
                    val handler = Handler()
                    handler.postDelayed( {
                        view.findViewById<RelativeLayout>(R.id.loading_panel_current_budget).visibility = View.GONE
                    }, 500)

                }
            },
            { error: VolleyError? ->
                run {
                    Log.e("Error rest response", error.toString())
                    val networkResponse: NetworkResponse? = error?.networkResponse
                    val jsonError: String? = String(networkResponse?.data!!)
                    val answer = JSONObject(jsonError ?: "")
                    if (answer.has("message")) {
                        Log.e("Error rest data", answer.getString("message") ?: "")
                        when (answer.getString("message")) {
                            "user not found" -> {
                                Toast.makeText(
                                    activity,
                                    "Could not find user",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            "no current budget" -> {
                                Toast.makeText(
                                    activity,
                                    "Could not find current budget",
                                    Toast.LENGTH_SHORT
                                ).show()
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

        view.toolbar_current_budget.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!,
                view.relative_layout_current_budget,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.menu_icon
                ),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon)
            )
        )

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

    private inner class BudgetPagerAdapter(activity: FragmentActivity?) : FragmentStateAdapter(activity!!){

        override fun createFragment(pos: Int): Fragment {
            return when (pos) {
                0 -> {
                    GeneralTabFragment(budget.date, budget.income)
                }
                1 -> {
                    CustomExpensesTabFragment(budget.customExpenses)
                }
                2 -> {
                    RegularExpensesTabFragment(budget.regularExpenses)
                }
                3 -> {
                    StrategiesTabFragment(budget.strategies)
                }
                else -> Fragment()
            }
        }

        override fun getItemCount(): Int {
            return PAGES
        }
    }

    companion object {
        private const val PAGES = 4

        var viewPager: ViewPager2? = null
    }
}