package com.example.dell.blackjack.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.dell.blackjack.R
import com.example.dell.blackjack.player
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_start.*
import java.text.SimpleDateFormat
import java.util.*

class StartMenu : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val gameView = Intent(this, MainActivity::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //初期処理
        startPrcs()

        //広告のビュー todo:広告クリック時処理を探す
        ad01.setOnClickListener {
            if(addChipView.visibility == View.VISIBLE){
                handler.removeCallbacks(runnable)
                count = 0
                chargeText.text = "Charge Complete !!"
                backToMenu.visibility = View.VISIBLE
                timerRunningView.visibility = View.GONE
            }
        }

        //広告
        ad01.loadAd(AdRequest.Builder().build())

        //チップ500未満時処理
        addChipView.setOnClickListener{}
        backToMenu.setOnClickListener {
            //チップ初期化
            player.setChip(this.applicationContext, player.PLAYERMONEY, player.FIRSTCHIP)
            backToMenu.visibility = View.GONE
            timerRunningView.visibility = View.VISIBLE
            addChipView.visibility = View.GONE
            startPrcs()
        }

        //各BET
        start500Bet.setOnClickListener {
            player.setBet(player.BET1)
            finish()
            startActivity(gameView)
        }
        start1000Bet.setOnClickListener {
            player.setBet(player.BET2)
            finish()
            startActivity(gameView)
        }
        start5000Bet.setOnClickListener {
            player.setBet(player.BET3)
            finish()
            startActivity(gameView)
        }


////////test用////////
        ////ゲーム画面へ遷移////
        debagStart.setOnClickListener{
            finish()
//            val intent = Intent(this,MainActivity::class.java)
            startActivity(gameView)
        }
        ////////////////
        ////chipを1,000,000にする
        debagChipEq1M.setOnClickListener{
            //自身のチップデータの読みこみ
            player.setChip(this.applicationContext, player.PLAYERMONEY, player.DEBAGMANYCHIP)
            startPrcs()
        }
        ////chipの格納されたプリファレンスを空にする
        debagChipClear.setOnClickListener{
            val chip = player.loadChip(this.applicationContext, player.PLAYERMONEY)
            if(chip != -1){
                player.removePref(this.applicationContext, player.PLAYERMONEY)
            }
            startPrcs()
        }
        ////チップを0にする
        debagChipEq0.setOnClickListener{
            val chip = player.loadChip(this.applicationContext, player.PLAYERMONEY)
            if(chip != -1){
                player.setChip(this.applicationContext, player.PLAYERMONEY,0)
            }
            startPrcs()
        }
////////////////////


    }
    /**
     * タイマーの作成
     */
    val handler = Handler()
    val dataFormat = SimpleDateFormat("s", Locale.US)
    val waiting = 15
    var count = 0
    val period = 1000
    private val runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            count++
            chargeTime.text = (waiting-(dataFormat.format(count * period).toInt())).toString()
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

    /**
     * 画面読込処理
     */
    @SuppressLint("SetTextI18n")
    private fun startPrcs(){
        //自身のチップデータの読みこみ
        var chip = player.loadChip(this.applicationContext, player.PLAYERMONEY)

        //プリファレンスがないときは初期値を入れる
        if(chip == -1){
            player.setChip(this.applicationContext, player.PLAYERMONEY, player.FIRSTCHIP)
            chip = player.loadChip(this.applicationContext, player.PLAYERMONEY)
        }

        //チップ所持数で掛けられるBETの処理
        start500Bet.isEnabled = chip >= player.BET1
        start1000Bet.isEnabled = chip >= player.BET2*10
        start5000Bet.isEnabled = chip >= player.BET3*10

        //各BETが掛けられないときの処理
        if(!start500Bet.isEnabled){
            start500Bet.text = "chipOver\n${player.BET1}"
            //チップ追加処理
            addChipView.visibility = View.VISIBLE
            //タイマー開始
            handler.post(runnable)
        }else{
            start500Bet.text = "${player.BET1}"
        }
        if(!start1000Bet.isEnabled){
            start1000Bet.text = "chipOver\n${player.BET2*10}"
        }else{
            start1000Bet.text = "${player.BET2}"
        }
        if(!start5000Bet.isEnabled){
            start5000Bet.text = "chipOver\n${player.BET3*10}"
        }else{
            start5000Bet.text = "${player.BET3}"
        }

        //画面に表示
        player.ownChip = chip
        nowChipStartMenu.text = chip.toString()
    }
}
