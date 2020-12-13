package edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.budget.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textview.MaterialTextView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import java.math.BigDecimal
import java.util.*

class GeneralTabFragment(private val date: Date, private val income: BigDecimal) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_current_budget_general_tab, container, false)
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = date
        view.findViewById<MaterialTextView>(R.id.text_view_current_budget_general_tab_date).text = String.format("%d-%d-%d", dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH))
        view.findViewById<MaterialTextView>(R.id.text_view_current_budget_general_tab_income).text = income.toString()
        return view
    }
}