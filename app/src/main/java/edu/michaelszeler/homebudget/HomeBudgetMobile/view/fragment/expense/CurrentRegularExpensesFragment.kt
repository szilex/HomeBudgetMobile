package edu.michaelszeler.homebudget.HomeBudgetMobile.view.fragment.expense

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.RegularExpenseEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.card.expense.RegularExpenseCardRecyclerViewAdapter
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.decoration.CustomGridItemDecoration
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.listener.DeleteRegularExpenseOnClickListener
import kotlinx.android.synthetic.main.fragment_current_regular_expenses.view.*
import org.json.JSONArray
import org.json.JSONObject

class CurrentRegularExpensesFragment : Fragment(), DeleteRegularExpenseOnClickListener {

    private lateinit var sessionManager: SessionManager
    private lateinit var regularExpenseToDelete: RegularExpenseEntry
    private lateinit var adapter: RegularExpenseCardRecyclerViewAdapter
    private lateinit var regularExpenseEntryList: MutableList<RegularExpenseEntry>
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_current_regular_expenses, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_current_regular_expenses)

        view.toolbar_current_regular_expenses.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!,
                view.nested_scroll_view_current_regular_expenses,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon))
        )

        val token = sessionManager.getToken()!!
        requestQueue = Volley.newRequestQueue(activity)

        val jsonArrayRequest = object: JsonArrayRequest(
            Method.GET,
            "http://10.0.2.2:8080/expense",
            null,
            {
                    response: JSONArray? ->
                run {
                    Log.e("Rest Response", response.toString())

                    view.recycler_view_current_regular_expenses.setHasFixedSize(true)
                    val gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)

                    view.recycler_view_current_regular_expenses.layoutManager = gridLayoutManager
                    regularExpenseEntryList = RegularExpenseEntry.convertToRegularExpenseList(response.toString()).toMutableList()
                    adapter = RegularExpenseCardRecyclerViewAdapter(regularExpenseEntryList, fragmentManager)
                    adapter.setDeleteRegularExpenseOnClickListener(this)
                    view.recycler_view_current_regular_expenses.adapter = adapter

                    val largePadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing)
                    val smallPadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing_small)
                    view.recycler_view_current_regular_expenses.addItemDecoration(CustomGridItemDecoration(largePadding, smallPadding))

                    view.relative_layout_current_regular_expenses.visibility = View.VISIBLE
                    view.loading_panel_current_regular_expenses.visibility = View.GONE
                }
            },
            {
                    error: VolleyError? ->
                run {
                    Log.e("Error rest response", error.toString())
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

        requestQueue.add(jsonArrayRequest)

        FragmentNavigationUtility.setUpMenuButtons((activity as NavigationHost), view)

        return view
    }

    override fun setRegularExpense(expense: RegularExpenseEntry) {
        this.regularExpenseToDelete = expense
    }

    override fun onClick(v: View?) {
        if (this::regularExpenseToDelete.isInitialized) {
            val token = sessionManager.getToken()!!
            val jsonObjectRequest = object: JsonObjectRequest(
                    Method.DELETE,
                    String.format("http://10.0.2.2:8080/expense?id=%d", regularExpenseToDelete.id),
                    null,
                    {
                        response: JSONObject? ->
                        run {
                            Log.e("Rest Response", response.toString())
                            regularExpenseEntryList.removeAt(regularExpenseEntryList.indexOf(regularExpenseToDelete))
                            adapter.notifyDataSetChanged()
                            Toast.makeText(activity, "Expense deleted!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    {
                        error: VolleyError? ->
                        run {
                            Log.e("Error rest response", error.toString())
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
                                        "Expense id not specified" -> {
                                            Toast.makeText(activity, "Could not delete expense", Toast.LENGTH_SHORT).show()
                                        }
                                        "Expense not found" -> {
                                            Toast.makeText(activity, "Strategy has not been found", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(activity, "Cannot delete strategy", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_back, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
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