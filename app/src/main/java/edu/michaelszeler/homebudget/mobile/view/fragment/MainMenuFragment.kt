package edu.michaelszeler.homebudget.mobile.view.fragment

import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import edu.michaelszeler.homebudget.mobile.R
import edu.michaelszeler.homebudget.mobile.session.SessionManager
import edu.michaelszeler.homebudget.mobile.utils.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.mobile.utils.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.mobile.view.fragment.user.LoginFragment
import kotlinx.android.synthetic.main.fragment_main_menu.view.*

class MainMenuFragment : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_main_menu)

        view.toolbar_main_menu.setNavigationOnClickListener(
            NavigationIconClickListener(
            activity!!,
            view.nested_scroll_view_main_menu,
            AccelerateDecelerateInterpolator(),
            ContextCompat.getDrawable(context!!, R.drawable.menu_icon),
            ContextCompat.getDrawable(context!!, R.drawable.menu_icon))
        )

        view.text_view_main_menu_welcome.text = String.format("Welcome, %s", sessionManager.getUserDetails()?.get("login") ?: "user" )

        FragmentNavigationUtility.setUpMenuButtons((activity as NavigationHost), view)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_log_out -> {
                sessionManager.logoutUser((activity as NavigationHost))
                Toast.makeText(context, "Log out successful", Toast.LENGTH_SHORT).show()
                (activity as NavigationHost).navigateTo(LoginFragment(), false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}