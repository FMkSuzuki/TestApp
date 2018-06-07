package com.example.dell.blackjack.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.dell.blackjack.R
import kotlinx.android.synthetic.main.activity_rule.*

class RuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule)

        back.setOnClickListener{
            finish()
        }

    }
}
