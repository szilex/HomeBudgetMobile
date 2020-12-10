package edu.michaelszeler.homebudget.HomeBudgetMobile.fragment

import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import kotlinx.android.synthetic.main.fragment_current_budget.view.*

class CurrentBudgetFragment : Fragment() {

    private lateinit var sessionManager : SessionManager

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

        view.toolbar_current_budget.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!,
                view.nested_scroll_view_current_budget,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon))
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
}