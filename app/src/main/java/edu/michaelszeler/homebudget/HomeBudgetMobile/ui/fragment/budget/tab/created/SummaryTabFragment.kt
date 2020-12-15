package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.budget.tab.created

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.budget.BudgetEntry
import kotlinx.android.synthetic.main.fragment_new_budget_summary_tab.view.*

class SummaryTabFragment(private val budgetEntry: BudgetEntry) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_budget_summary_tab, container, false)
        view.button_new_budget_summary_tab_create.setOnClickListener {
            Toast.makeText(activity, "Button clicked", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}