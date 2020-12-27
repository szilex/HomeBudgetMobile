package edu.michaelszeler.homebudget.HomeBudgetMobile.view.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.validation.TextInputValidator
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.fragment.budget.tab.created.FragmentCallback
import kotlinx.android.synthetic.main.dialog_custom_expense.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomExpenseDialog : DialogFragment() {

    companion object {

        const val NAME = "New custom expense"

        private const val KEY_COMPONENT = "component"
        private const val KEY_STATUS = "status"
        private const val KEY_NAME = "name"
        private const val KEY_CATEGORY = "category"
        private const val KEY_AMOUNT = "amount"
        private const val KEY_DATE = "date"
        private const val KEY_CATEGORIES = "categories"

        fun newInstance(categories : List<String>): CustomExpenseDialog {
            val args = Bundle()
            args.putStringArrayList(KEY_CATEGORIES, ArrayList(categories))
            val dialog = CustomExpenseDialog()
            dialog.arguments = args
            dialog.onSaveInstanceState(args)
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_custom_expense, container, false)
        val spinner = view.findViewById<AutoCompleteTextView>(R.id.spinner_dialog_custom_expense_category)
        val adapter = ArrayAdapter(activity as Context, R.layout.dropdown_simple_item, arguments?.get(KEY_CATEGORIES) as ArrayList<String>)
        spinner.setAdapter(adapter)
        spinner.setOnClickListener {
            spinner.showDropDown()
        }
        val calendar = Calendar.getInstance()
        val editText = view.findViewById(R.id.edit_text_dialog_custom_expense_date) as EditText
        val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
            editText.setText(sdf.format(calendar.time))
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)
        }
        editText.setOnClickListener{
            DatePickerDialog(activity as AppCompatActivity, R.style.DialogTheme, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        val parent: FragmentCallback = targetFragment as FragmentCallback

        view.button_dialog_custom_expense_tab_add.setOnClickListener {
            val name = view.text_input_dialog_custom_expense_name.text.toString()
            val category = view.spinner_dialog_custom_expense_category.text.toString()
            val amount = view.edit_text_dialog_custom_expense_amount.text.toString()
            val date = view.edit_text_dialog_custom_expense_date.text.toString()

            if (TextInputValidator.isExpenseNameValid(name) && category.isNotBlank() && TextInputValidator.isAmountValid(amount) && TextInputValidator.isDateValid(date)) {
                val args = Bundle()
                args.putString(KEY_COMPONENT, "custom expense dialog")
                args.putString(KEY_STATUS, "success")
                args.putString(KEY_NAME, name)
                args.putString(KEY_CATEGORY, category)
                args.putString(KEY_AMOUNT, amount)
                args.putString(KEY_DATE, date)
                parent.callback(args)
                dismiss()
            } else {
                if (!TextInputValidator.isExpenseNameValid(name)) {
                    view.text_input_dialog_custom_expense_name.error = "Please enter valid name"
                }
                if (view.spinner_dialog_custom_expense_category.text.isBlank()) {
                    view.spinner_dialog_custom_expense_category.error = "Please choose category"
                }
                if (!TextInputValidator.isAmountValid(amount)) {
                    view.edit_text_dialog_custom_expense_amount.error = "Please enter valid amount"
                }
                if (!TextInputValidator.isDateValid(date)) {
                    view.edit_text_dialog_custom_expense_date.error = "Please enter valid date"
                }
            }
        }

        setUpTextChangedListener(view.text_input_dialog_custom_expense_name)
        setUpTextChangedListener(view.spinner_dialog_custom_expense_category)
        setUpTextChangedListener(view.edit_text_dialog_custom_expense_amount)
        setUpTextChangedListener(view.edit_text_dialog_custom_expense_date)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.button_dialog_custom_expense_close.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

}