package edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.strategy

import android.os.Bundle
import android.util.Base64
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
import com.android.volley.toolbox.Volley
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.strategy.StrategyCardRecyclerViewAdapter
import edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.strategy.StrategyGridItemDecoration
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.strategy.StrategyEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.MainMenuFragment
import kotlinx.android.synthetic.main.fragment_archive_strategies.view.*
import org.json.JSONArray
import org.json.JSONObject

class ArchiveStrategiesFragment : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_archive_strategies, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_archive_strategies)

        view.toolbar_archive_strategies.setNavigationOnClickListener(
            NavigationIconClickListener(
            activity!!,
            view.nested_scroll_view_archive_strategies,
            AccelerateDecelerateInterpolator(),
            ContextCompat.getDrawable(context!!, R.drawable.menu_icon),
            ContextCompat.getDrawable(context!!, R.drawable.menu_icon))
        )

        val login = sessionManager.getUserDetails()?.get("login")
        val password = sessionManager.getUserDetails()?.get("password")
        val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

        val jsonArrayRequest = object: JsonArrayRequest(
            Method.GET,
            "http://10.0.2.2:8080/strategy/archive",
            null,
            {
                    response: JSONArray? ->
                run {
                    Log.e("Rest Response", response.toString())

                    view.recycler_view_archive_strategies.setHasFixedSize(true)
                    val gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)

                    view.recycler_view_archive_strategies.layoutManager = gridLayoutManager
                    val adapter = StrategyCardRecyclerViewAdapter(StrategyEntry.convertToStrategyList(response.toString()), fragmentManager)

                    view.recycler_view_archive_strategies.adapter = adapter

                    val largePadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing)
                    val smallPadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing_small)
                    view.recycler_view_archive_strategies.addItemDecoration(StrategyGridItemDecoration(largePadding, smallPadding))

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
                            "no strategies found" -> {
                                Toast.makeText(activity, "Could not find any archive strategies", Toast.LENGTH_SHORT).show()
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