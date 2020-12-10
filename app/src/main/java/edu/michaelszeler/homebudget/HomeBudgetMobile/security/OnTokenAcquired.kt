package edu.michaelszeler.homebudget.HomeBudgetMobile.security

import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.os.Bundle

class OnTokenAcquired : AccountManagerCallback<Bundle> {
    override fun run(future: AccountManagerFuture<Bundle>?) {
        //val bundle: Bundle = future.getResult()
    }
}