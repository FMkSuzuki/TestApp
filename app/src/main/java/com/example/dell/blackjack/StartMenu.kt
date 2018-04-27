package com.example.dell.blackjack

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_start.*
import java.text.SimpleDateFormat
import java.util.*

class StartMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //タイマーの作成
        val handler = Handler()
        val dataFormat = SimpleDateFormat("s", Locale.US)
        val waiting = 15
        var count: Int = 0
        val runnable = object : Runnable {
            val period = 1000
            override fun run() {
                count++
                chargeTime.setText((waiting-(dataFormat.format(count * period).toInt())).toString())
                if(count > waiting){
                    timerRunningView.visibility = View.GONE
                    chargeText.text = "Charge Complete !!"
                    backToMenu.visibility = View.VISIBLE
                    count = 0
                    return
                }
                handler.postDelayed(this, period.toLong())
            }
        }

        ////読込時処理
        fun startPrcs(){
            //自身のチップデータの読みこみ
            var chip = loadChip(this.applicationContext,PLAYERMONEY)
            if(chip == -1){
                //プリファレンスがないときは初期値を入れる
                setChip(this.applicationContext,PLAYERMONEY,FIRSTCHIP)
                chip = loadChip(this.applicationContext, PLAYERMONEY)
            }
            start500Bet.isEnabled = chip >= BET1
            start1000Bet.isEnabled = chip >= BET2*10
            start5000Bet.isEnabled = chip >= BET3*10
            if(!start500Bet.isEnabled){
                start500Bet.text = "chipOver\n${BET1}"
                //チップ追加処理
                addChipView.visibility = View.VISIBLE
                //タイマー開始
                handler.post(runnable)
            }else{
                start500Bet.text = "$BET1"
            }
            if(!start1000Bet.isEnabled){
                start1000Bet.text = "chipOver\n${BET2*10}"
            }else{
                start1000Bet.text = "$BET2"
            }
            if(!start5000Bet.isEnabled){
                start5000Bet.text = "chipOver\n${BET3*10}"
            }else{
                start5000Bet.text = "$BET3"
            }
            //画面に表示
            player.ownChip = chip
            nowChipStartMenu.text = chip.toString()
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////初期処理
        startPrcs()
        //広告のビュー
        adViewMenu.setOnClickListener {
            if(addChipView.visibility == View.VISIBLE){
                handler.removeCallbacks(runnable)
                count = 0
                chargeText.text = "Charge Complete !!"
                backToMenu.visibility = View.VISIBLE
                timerRunningView.visibility = View.GONE
            }
        }
        //ダミー(後ろのボタンを押させない)
        addChipView.setOnClickListener{
        }
        backToMenu.setOnClickListener {
            //チップ初期化
            setChip(this.applicationContext,PLAYERMONEY,FIRSTCHIP)
            backToMenu.visibility = View.GONE
            timerRunningView.visibility = View.VISIBLE
            addChipView.visibility = View.GONE
            startPrcs()
        }


        //500bet
        start500Bet.setOnClickListener {
            player.setBet(BET1)
            finish()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        //1000bet
        start1000Bet.setOnClickListener {
            player.setBet(BET2)
            finish()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        //5000bet
        start5000Bet.setOnClickListener {
            player.setBet(BET3)
            finish()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }


////////test用////////
        ////ゲーム画面へ遷移////
        debagStart.setOnClickListener{
            finish()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        ////////////////
        ////chipを1,000,000にする
        debagChipEq1M.setOnClickListener{
            //自身のチップデータの読みこみ
            setChip(this.applicationContext,PLAYERMONEY,DEBAGMANYCHIP)
            startPrcs()
        }
        ////chipの格納されたプリファレンスを空にする
        debagChipClear.setOnClickListener{
            val chip = loadChip(this.applicationContext,PLAYERMONEY)
            if(chip != -1){
                removePref(this.applicationContext,PLAYERMONEY)
            }
            startPrcs()
        }
        ////チップを0にする
        debagChipEq0.setOnClickListener{
            val chip = loadChip(this.applicationContext,PLAYERMONEY)
            if(chip != -1){
                setChip(this.applicationContext,PLAYERMONEY,0)
            }
            startPrcs()
        }

////////////////////
    }
}
