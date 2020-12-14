package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.user

import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import kotlinx.android.synthetic.main.fragment_my_account.view.*

class MyAccountFragment : Fragment() {

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

        val view = inflater.inflate(R.layout.fragment_my_account, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_my_account)

        view.toolbar_my_account.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!,
                view.nested_scroll_view_my_account,
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