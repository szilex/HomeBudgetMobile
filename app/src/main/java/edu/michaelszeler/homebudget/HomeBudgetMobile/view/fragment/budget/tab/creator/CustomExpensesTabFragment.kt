package edu.michaelszeler.homebudget.HomeBudgetMobile.view.fragment.budget.tab.creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.CustomExpenseEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.card.expense.CustomExpenseCardRecyclerViewAdapter
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.decoration.CustomGridItemDecoration
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.dialog.CustomExpenseDialogFragment
import kotlinx.android.synthetic.main.fragment_new_budget_custom_expenses_tab.view.*
import java.math.BigDecimal
import java.text.ParsePosition
import java.text.SimpleDateFormat

class CustomExpensesTabFragment(private val categories : List<String> ,private val expenses : List<CustomExpenseEntry>) : Fragment(), FragmentCallback {

    private lateinit var parent: FragmentCallback
    private lateinit var mutableExpenses : MutableList<CustomExpenseEntry>
    private lateinit var adapter: CustomExpenseCardRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mutableExpenses = expenses.toMutableList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view : View = inflater.inflate(R.layout.fragment_new_budget_custom_expenses_tab, container, false)

        view.recycler_view_new_budget_custom_expenses_tab.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)

        view.recycler_view_new_budget_custom_expenses_tab.layoutManager = gridLayoutManager
        adapter = CustomExpenseCardRecyclerViewAdapter(mutableExpenses)
        view.recycler_view_new_budget_custom_expenses_tab.adapter = adapter

        val largePadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing)
        val smallPadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing_small)
        view.recycler_view_new_budget_custom_expenses_tab.addItemDecoration(CustomGridItemDecoration(largePadding, smallPadding))

        view.button_new_budget_custom_expense_tab_add.setOnClickListener {
            val dialog = CustomExpenseDialogFragment.newInstance(categories)
            dialog.setTargetFragment(this, 0)
            dialog.show(fragmentManager!!, CustomExpenseDialogFragment.NAME)
        }

        return view
    }

    override fun callback(args: Bundle) {
        val name = args.getString("name")
        val category = args.getString("category")
        val amount = BigDecimal(args.getString("amount"))
        val position = ParsePosition(0)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = simpleDateFormat.parse(args.getString("date"), position)
        val customExpense = CustomExpenseEntry(name!!, category!!, amount, date)
        mutableExpenses.add(0, customExpense)
        args.putString("tab", "custom expenses")
        parent.callback(args)
        adapter.notifyDataSetChanged()

    }

    fun setFragmentCallback(parent: FragmentCallback) {
        this.parent = parent
    }
}