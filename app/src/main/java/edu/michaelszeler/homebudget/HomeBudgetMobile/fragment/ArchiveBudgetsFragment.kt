package edu.michaelszeler.homebudget.HomeBudgetMobile.fragment

import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.navigation.NavigationIconClickListener
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.strategy.StrategyCardRecyclerViewAdapter
import edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.strategy.StrategyEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.strategy.StrategyGridItemDecoration
import kotlinx.android.synthetic.main.fragment_archive_budgets.view.*

class ArchiveBudgetsFragment : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_archive_budgets, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_archive_budgets)

        view.toolbar_archive_budgets.setNavigationOnClickListener(
            NavigationIconClickListener(
            activity!!,
            view.nested_scroll_view_archive_budgets,
            AccelerateDecelerateInterpolator(),
            ContextCompat.getDrawable(context!!, R.drawable.menu_icon),
            ContextCompat.getDrawable(context!!, R.drawable.menu_icon))
        )

        view.recycler_view_archive_budgets.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position % 3 == 2) 2 else 1
            }
        }

        view.recycler_view_archive_budgets.layoutManager = gridLayoutManager
        val adapter = StrategyCardRecyclerViewAdapter(StrategyEntry.convertToStrategyList(resources), fragmentManager)
        view.recycler_view_archive_budgets.adapter = adapter

        val largePadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing)
        val smallPadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing_small)
        view.recycler_view_archive_budgets.addItemDecoration(StrategyGridItemDecoration(largePadding, smallPadding))

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