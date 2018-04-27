package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start.*
import org.jetbrains.anko.button
import org.jetbrains.anko.linearLayout

class MainActivity : AppCompatActivity() {
    private var interstitialAd: InterstitialAd? = null //インテンション広告用
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //初期処理
        zoneReset(0)

        //インテンション広告の生成
        interstitialAd = newInterstitialAd()
        loadInterstitial()

        //カードを引く
        hit.setOnClickListener {
            drawCard(hand,handZone,true)
            countCards(endsCards)
            val pCS = calcCardScore(hand,playerCS,true)
            val dCS = calcCardScore(dealer,dealerCS,false)

            //ブラックジャックの時はhitを止める(standを押させる)
            if( pCS == BLACKJACK){
                hit.isEnabled = false
                return@setOnClickListener
            }

            //22以上<Bust>で敗北
            if(pCS > BLACKJACK){
                openCard(dealerZone)
                hit.isEnabled = false
                stand.isEnabled = false
                result.text = DUELRESLT[cmpScore(pCS,dCS, player)]
                nextSet()
            }
        }

        //今の持ち札で対戦
        stand.setOnClickListener{
            stand.isEnabled = false
            hit.isEnabled = false
            drawCard(dealer,dealerZone,false)
            //結果
            val pCS = calcCardScore(hand,playerCS,true)
            val dCS = calcCardScore(dealer,dealerCS,false)
            result.text = DUELRESLT[cmpScore(pCS,dCS, player)]

            nextSet()
        }

        //次のゲームを始める
        nextGame.setOnClickListener {
            zoneReset(1)
            nextGame.visibility = View.GONE
            backTop1.visibility = View.GONE
        }

        //TOPに戻る
        backTop1.setOnClickListener {
            nextGame.visibility = View.GONE
            backTop1.visibility = View.GONE
            showInterstitial()
        }

        //チップ不足
        socView.setOnClickListener {}//下位画面ボタン操作制御
        backTop2.setOnClickListener {
            val intent = Intent(this,StartMenu::class.java)
            finish()
            startActivity(intent)
        }

        //ヘルプ表示/ゲーム画面再表示
        help.setOnClickListener{
            capZone.visibility = View.VISIBLE
        }
        backGame1.setOnClickListener {
            capZone.visibility = View.GONE
        }

        //広告
        ad02.loadAd(AdRequest.Builder().build())
    }
    /**
     * 場のリセット(初回:0・継続:1)
     */
    @SuppressLint("SetTextI18n")
    private fun zoneReset(status:Int){
        //場のカード情報の削除
        handZone.removeAllViews()
        dealerZone.removeAllViews()
        result.text = ""
        hit.text = "hit"
        stand.text = "stand"
        //残りチップの判定
        if(!player.callChip()){
            socView.visibility = View.VISIBLE
        }
        if(status == 0){
            setCaption(caption00)
            makeDeck(DECKCONT)
        }
        //手札生成(プレイヤー、ディーラー)
        makeHand(handZone, hand, true)
        makeHand(dealerZone, dealer, false)
        //合計値の算出
        calcCardScore(hand, playerCS, true)
        calcCardScore(dealer, dealerCS, false)
        //山札の残り
        countCards(endsCards)
        //ボタンの活性化
        hit.isEnabled = true
        stand.isEnabled = true
        //自身のチップデータの読みこみ
        val chip = loadChip(this.applicationContext, PLAYERMONEY)
        ////仮置きtest(最低限のベットを行う)
        //player.setBet(BET1)
        //情報の画面表示
        ownChip.text = "chip: $chip"
        bet.text = "bet: ${player.betChip}"
        //初回カードの判定
        val playerFstScore= calcpt(hand,false)
        val dealerFstScore= calcpt(dealer,true)
        if (playerFstScore == BLACKJACK) {
            //プレイヤー初回BJなら即勝負を掛けれるようにしとく(なくても良いやつ？)
            hit.isEnabled = false
            hit.text = "BJ"
        }
        if (dealerFstScore == BLACKJACK) {
            //ディーラーBJだと強制勝負
            openCard(dealerZone)
            dealerCS.text = "Dealer:$BLACKJACK <BJ>"
            calcCardScore(hand,playerCS,true)
            //プレイヤーの操作は不可
            hit.isEnabled = false
            hit.text = "BJ"
        }
//        resultViewBtn.removeAllViews()
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 次のゲームを始める
     */
    @SuppressLint("SetTextI18n")
    private fun nextSet(){
        ownChip.text = "chip: ${player.ownChip}"
        setChip(this.applicationContext, PLAYERMONEY, player.ownChip)
        nextGame.visibility = View.VISIBLE
        backTop1.visibility = View.VISIBLE
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     *インターナル広告
     *
     * */
    private fun showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (interstitialAd?.isLoaded == true) {
            interstitialAd?.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            goToNextLevel()
        }
    }
    private fun newInterstitialAd(): InterstitialAd {
        return InterstitialAd(this).apply {
            adUnitId = getString(R.string.interstitial_ad_unit_id)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    backTop1.isEnabled = true
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    backTop1.isEnabled = true
                }
                override fun onAdClosed() {
                    // Proceed to the next level.
                    goToNextLevel()
                }
            }
        }
    }
    private fun loadInterstitial() {
        // Disable the next level button and load the ad.
        backTop1.isEnabled = false
        val adRequest = AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template")
                .build()
        interstitialAd?.loadAd(adRequest)
    }
    private fun goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.
//        level.text = "Level " + (++currentLevel)
        interstitialAd = newInterstitialAd()
        loadInterstitial()
        player.resetBet()
        val intent = Intent(this,StartMenu::class.java)
        startActivity(intent)
        finish()
    }
    ////////////////////////////////////////////////////////////////////
}
