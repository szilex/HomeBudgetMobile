package edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.budget

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.budget.BudgetEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import kotlinx.android.synthetic.main.fragment_current_budget.view.*
import org.json.JSONObject

class CurrentBudgetFragment : Fragment() {

    private lateinit var sessionManager : SessionManager
    private lateinit var budget : BudgetEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_current_budget, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_current_budget)

        view.toolbar_current_budget.setNavigationOnClickListener(NavigationIconClickListener(activity!!, view.nested_scroll_view_current_budget, AccelerateDecelerateInterpolator(), ContextCompat.getDrawable(context!!, R.drawable.menu_icon), ContextCompat.getDrawable(context!!, R.drawable.menu_icon)))

        val login = sessionManager.getUserDetails()?.get("login")
        val password = sessionManager.getUserDetails()?.get("password")
        val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

        val jsonArrayRequest = object: JsonObjectRequest(
                Method.GET,
                "http://10.0.2.2:8080/budget",
                null,
                {
                    response: JSONObject? ->
                    run {
                        Log.e("Rest Response", response.toString())

                        budget = BudgetEntry.convertToBudget(response.toString())

                        //TODO create tab layout



                    }
                },
                {
                    error: VolleyError? ->
                    run {
                        Log.e("Error rest response", error.toString())
                        val networkResponse : NetworkResponse? = error?.networkResponse
                        val jsonError : String? = String(networkResponse?.data!!)
                        val answer = JSONObject(jsonError ?: "")
                        if (answer.has("message")) {
                            Log.e("Error rest data", answer.getString("message") ?: "")
                            when (answer.getString("message")) {
                                "user not found" -> {
                                    Toast.makeText(activity, "Could not find user", Toast.LENGTH_SHORT).show()
                                }
                                "no current budget" -> {
                                    Toast.makeText(activity, "Could not find current budget", Toast.LENGTH_SHORT).show()
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

        requestQueue.add(jsonArrayRequest)

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
}