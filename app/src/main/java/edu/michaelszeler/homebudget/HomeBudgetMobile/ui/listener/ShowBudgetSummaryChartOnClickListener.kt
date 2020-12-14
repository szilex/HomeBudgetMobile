package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.listener

import android.view.View
import androidx.fragment.app.FragmentManager
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.budget.BudgetSummaryEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.dialog.BudgetSummaryDialogFragment

class ShowBudgetSummaryChartOnClickListener(private var fragmentManager: FragmentManager?, private var budget : BudgetSummaryEntry?) : View.OnClickListener {
    override fun onClick(v: View?) {
        BudgetSummaryDialogFragment.newInstance(budget!!).show(fragmentManager!!, BudgetSummaryDialogFragment.NAME)
    }
}

