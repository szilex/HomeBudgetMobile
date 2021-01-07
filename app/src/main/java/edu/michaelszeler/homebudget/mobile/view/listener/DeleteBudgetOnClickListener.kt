package edu.michaelszeler.homebudget.mobile.view.listener

import android.view.View
import edu.michaelszeler.homebudget.mobile.model.budget.BudgetSummaryEntry

interface DeleteBudgetOnClickListener : View.OnClickListener {
    fun setBudget(budget: BudgetSummaryEntry)
}