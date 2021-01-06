package edu.michaelszeler.homebudget.mobile.view.listener

import android.view.View
import edu.michaelszeler.homebudget.mobile.model.expense.RegularExpenseEntry

interface DeleteRegularExpenseOnClickListener : View.OnClickListener {
    fun setRegularExpense(expense: RegularExpenseEntry)
}