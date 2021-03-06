package edu.michaelszeler.homebudget.mobile.view.fragment.strategy

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
import edu.michaelszeler.homebudget.mobile.R
import edu.michaelszeler.homebudget.mobile.model.strategy.StrategyEntry
import edu.michaelszeler.homebudget.mobile.session.SessionManager
import edu.michaelszeler.homebudget.mobile.utils.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.mobile.view.card.strategy.StrategyCardRecyclerViewAdapter
import edu.michaelszeler.homebudget.mobile.view.decoration.CustomGridItemDecoration
import edu.michaelszeler.homebudget.mobile.view.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.mobile.view.listener.DeleteStrategyOnClickListener
import kotlinx.android.synthetic.main.fragment_current_strategies.view.*
import org.json.JSONArray
import org.json.JSONObject

class CurrentStrategiesFragment : Fragment(), DeleteStrategyOnClickListener {

    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: StrategyCardRecyclerViewAdapter
    private lateinit var strategyEntryList: MutableList<StrategyEntry>
    private lateinit var requestQueue: RequestQueue

    private var strategyToDelete: StrategyEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_current_strategies, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_current_strategies)

        view.toolbar_current_strategies.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!,
                view.nested_scroll_view_current_strategies,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon))
        )

        val token = sessionManager.getToken()!!
        requestQueue = Volley.newRequestQueue(activity)

        val jsonArrayRequest = object: JsonArrayRequest(
            Method.GET,
            "http://10.0.2.2:8080/strategy",
            null,
            {
                    response: JSONArray? ->
                run {
                    Log.e("Rest Response", response.toString())

                    view.recycler_view_current_strategies.setHasFixedSize(true)
                    val gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)

                    view.recycler_view_current_strategies.layoutManager = gridLayoutManager
                    strategyEntryList = StrategyEntry.convertToStrategyList(response.toString()).toMutableList()
                    adapter = StrategyCardRecyclerViewAdapter(strategyEntryList, fragmentManager)
                    adapter.setDeleteStrategyOnClickListener(this)
                    view.recycler_view_current_strategies.adapter = adapter

                    val largePadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing)
                    val smallPadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing_small)
                    view.recycler_view_current_strategies.addItemDecoration(CustomGridItemDecoration(largePadding, smallPadding))

                    view.relative_layout_current_strategies.visibility = View.VISIBLE
                    view.loading_panel_current_strategies.visibility = View.GONE
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

    override fun setStrategy(strategy: StrategyEntry) {
        this.strategyToDelete = strategy
    }

    override fun onClick(v: View?) {
        if (this.strategyToDelete != null) {
            val token = sessionManager.getToken()!!
            val jsonObjectRequest = object: JsonObjectRequest(
                    Method.DELETE,
                    String.format("http://10.0.2.2:8080/strategy?id=%d", this.strategyToDelete!!.id),
                    null,
                    {
                        response: JSONObject? ->
                        run {
                            Log.e("Rest Response", response.toString())
                            strategyEntryList.removeAt(strategyEntryList.indexOf(this.strategyToDelete!!))
                            adapter.notifyDataSetChanged()
                            strategyToDelete = null
                            Toast.makeText(activity, "Strategy deleted!", Toast.LENGTH_SHORT).show()
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
                                        "Strategy id not specified" -> {
                                            Toast.makeText(activity, "Could not delete strategy", Toast.LENGTH_SHORT).show()
                                        }
                                        "Strategy not found" -> {
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
                    //headers["Content-Type"] = "application/json"
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
