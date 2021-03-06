package edu.michaelszeler.homebudget.mobile.view.fragment.budget.tab.creator

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import edu.michaelszeler.homebudget.mobile.R
import edu.michaelszeler.homebudget.mobile.utils.validation.TextInputValidator
import kotlinx.android.synthetic.main.fragment_new_budget_general_tab.view.*
import java.text.SimpleDateFormat
import java.util.*

class GeneralTabFragment() : Fragment() {

    private lateinit var parent: FragmentCallback
    private val monthAndYearFormat = SimpleDateFormat("yyyy-MM", Locale.ENGLISH)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_budget_general_tab, container, false)
        val amountEditText = view.edit_text_new_budget_general_tab_amount
        val dateEditText = view.findViewById(R.id.edit_text_new_budget_general_tab_date) as EditText
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateEditText.setText(monthAndYearFormat.format(calendar.time))
        }
        dateEditText.setOnClickListener{
            DatePickerDialog(activity as AppCompatActivity, R.style.DialogTheme, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        view.button_new_budget_general_tab_confirm.setOnClickListener {
            if (TextInputValidator.isShortDateValid(dateEditText.text.toString()) && TextInputValidator.isAmountValid(amountEditText.text.toString())) {
                val args = Bundle()
                args.putString("tab", "general")
                args.putString("date", dateFormat.format(calendar.time))
                args.putString("amount", amountEditText.text.toString())
                parent.callback(args)
            } else {
                if (!TextInputValidator.isDateValid(dateEditText.text.toString())) {
                    dateEditText.error = "Choose correct date"
                }
                if (!TextInputValidator.isAmountValid(amountEditText.text.toString())) {
                    amountEditText.error = "Enter correct amount"
                }
            }
        }

        setUpTextChangedListener(dateEditText)
        setUpTextChangedListener(amountEditText)

        return view
    }

    private fun setUpTextChangedListener(editText: EditText) {
        editText.addTextChangedListener(object :
                TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { editText.error = null }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun setFragmentCallback(parent: FragmentCallback) {
        this.parent = parent
    }
}