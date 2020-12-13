package edu.michaelszeler.homebudget.HomeBudgetMobile.navigation

import android.view.View
import com.google.android.material.button.MaterialButton
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.budget.ArchiveBudgetsFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.budget.CurrentBudgetFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.budget.NewBudgetFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.expense.CurrentRegularExpensesFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.expense.NewRegularExpenseFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.strategy.ArchiveStrategiesFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.strategy.CurrentStrategiesFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.strategy.NewStrategyFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.user.MyAccountFragment

class FragmentNavigationUtility {
    companion object {
        fun setUpMenuButtons(navigationHost: NavigationHost, view : View) {
            view.findViewById<MaterialButton>(R.id.button_new_budget).setOnClickListener {
                navigationHost.navigateTo(NewBudgetFragment(), false)
            }

            view.findViewById<MaterialButton>(R.id.button_current_budget).setOnClickListener {
                navigationHost.navigateTo(CurrentBudgetFragment(), false)
            }

            view.findViewById<MaterialButton>(R.id.button_archive_budgets).setOnClickListener {
                navigationHost.navigateTo(ArchiveBudgetsFragment(), false)
            }

            view.findViewById<MaterialButton>(R.id.button_new_regular_expense).setOnClickListener {
                navigationHost.navigateTo(NewRegularExpenseFragment(), false)
            }

            view.findViewById<MaterialButton>(R.id.button_current_regular_expenses).setOnClickListener {
                navigationHost.navigateTo(CurrentRegularExpensesFragment(), false)
            }

            view.findViewById<MaterialButton>(R.id.button_new_strategy).setOnClickListener {
                navigationHost.navigateTo(NewStrategyFragment(), false)
            }

            view.findViewById<MaterialButton>(R.id.button_current_strategies).setOnClickListener {
                navigationHost.navigateTo(CurrentStrategiesFragment(), false)
            }

            view.findViewById<MaterialButton>(R.id.button_archive_strategies).setOnClickListener {
                navigationHost.navigateTo(ArchiveStrategiesFragment(), false)
            }
            view.findViewById<MaterialButton>(R.id.button_my_account).setOnClickListener {
                navigationHost.navigateTo(MyAccountFragment(), false)
            }
        }
    }
}