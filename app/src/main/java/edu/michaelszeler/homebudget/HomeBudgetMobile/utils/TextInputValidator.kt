package edu.michaelszeler.homebudget.HomeBudgetMobile.utils

class TextInputValidator {

    companion object {
        fun isLoginValid(login: String) : Boolean {
            return login.isNotBlank() && login.matches("[A-Za-z][\\w]{5,}".toRegex())
        }

        fun isPasswordValid(password: String) : Boolean {
            return password.isNotBlank() && password.matches("[\\w!@#$%^&*_,.?<>]{6,}".toRegex())
        }

        fun isMailValid(mail: String): Boolean {
            return mail.isNotBlank() && mail.matches("[\\w._]+@([\\w]\\.)+(\\.[\\w])".toRegex())
        }

        fun isNameValid(name: String): Boolean {
            return name.isNotBlank() && name.matches("[\\w]+".toRegex())
        }

        fun isExpenseNameValid(name: String): Boolean {
            return name.isNotBlank() && name.matches("[\\w]([\\w\\s])*".toRegex())
        }

        fun isStrategyNameValid(name: String): Boolean {
            return name.isNotBlank() && name.matches("[\\w]([\\w\\s])*".toRegex())
        }

        fun isStrategyDescriptionValid(name: String): Boolean {
            return name.isNotBlank() && name.matches("[\\w]([\\w\\s])*".toRegex())
        }

        fun isAmountValid(amount: String): Boolean {
            return amount.isNotBlank() && amount.matches("[1-9]\\d*(\\.|,[\\d]{1,2})?".toRegex())
        }

        fun isDateValid(date: String): Boolean {
            return date.isNotBlank() && date.matches("\\d{4}-\\d{2}-\\d{1,2}".toRegex())
        }

        fun isMonthAmountValid(amount: String): Boolean {
            return amount.isNotBlank() && amount.matches("[1-9](\\d)*".toRegex())
        }
    }
}