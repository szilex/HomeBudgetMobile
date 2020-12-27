package edu.michaelszeler.homebudget.HomeBudgetMobile.view.listener

import android.view.View
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.RegularExpenseEntry

interface DeleteRegularExpenseOnClickListener : View.OnClickListener {
    fun setRegularExpense(expense: RegularExpenseEntry)
}