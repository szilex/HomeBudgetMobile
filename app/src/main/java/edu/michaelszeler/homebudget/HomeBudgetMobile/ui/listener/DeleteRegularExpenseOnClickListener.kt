package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.listener

import android.view.View
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.RegularExpenseEntry

interface DeleteRegularExpenseOnClickListener : View.OnClickListener {
    fun setRegularExpense(expense: RegularExpenseEntry)
}