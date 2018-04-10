package com.nicolasbahamon.cryptocoins

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_financial_resume.*

class FinancialResume : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_resume)

        textView81.setText("hola mundo")
    }
}
