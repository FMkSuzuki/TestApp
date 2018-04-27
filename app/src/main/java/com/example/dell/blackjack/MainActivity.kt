package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.button
import org.jetbrains.anko.linearLayout

//fun zoneReset(status:Int){}
//fun nextSet(){}

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //インテント
        val startMenuIntent = Intent(this,StartMenu::class.java)
        val ruleIntent = Intent(this,RuleActivity::class.java)



        /**
         * 場のリセット(初回:0・継続:1)
         */
        fun zoneReset(status:Int){
            //場のカード情報の削除
            handZone.removeAllViews()
            dealerZone.removeAllViews()
            result.text = ""
            //残りチップの判定
            if(!player.callChip()){
                //test あとでかく
            }

            if(status == 0){
                setCaption(capZone)
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
            var playerFstScore= calcpt(hand,false)
            var dealerFstScore= calcpt(dealer,true)
            if (playerFstScore == BLACKJACK) {
                //プレイヤー初回BJなら即勝負を掛けれるようにしとく(なくても良いやつ？)
                hit.isEnabled = false
                hit.text = "player\nBJ"
            }
            if (dealerFstScore == BLACKJACK) {
                //ディーラーBJだと強制勝負
                openCard(dealerZone)
                dealerCS.text = "Dealer:$BLACKJACK <BJ>"
                calcCardScore(hand,playerCS,true)
                //プレイヤーの操作は不可
                hit.isEnabled = false
                hit.text = "Dealer\nBJ"
            }
            resultViewBtn.removeAllViews()
        }

        /**
         * 次のゲームを始める
         */
        fun nextSet(){
            ownChip.text = "chip: ${player.ownChip}"
            setChip(this.applicationContext, PLAYERMONEY, player.ownChip)
            resultViewBtn.linearLayout {
                //次のゲームを始める
                button{
                    text = "NEXT"
                }.lparams(width = resultViewBtn.width/2){
                    gravity = Gravity.NO_GRAVITY
                }.setOnClickListener {
                    zoneReset(1)
                }
                button{
                    text = "TOP"
                }.lparams(width = resultViewBtn.width/2){
                    gravity = Gravity.NO_GRAVITY
                }.setOnClickListener {
                    player.resetBet()
                    startActivity(startMenuIntent)
                    finish()
                }
            }
        }

        ////初期処理////
        zoneReset(0)
        ////////////////

        ////カードを引く////
        hit.setOnClickListener {
            drawCard(hand,handZone,true)
            countCards(endsCards)
            val pCS = calcCardScore(hand,playerCS,true)
            val dCS = calcCardScore(dealer,dealerCS,false)
            //ブラックジャックの時はhitを止める(standは押させる)
            if( pCS == BLACKJACK){
                hit.isEnabled = false
                return@setOnClickListener
            }
            //22以上<Bust>で敗北
            if(pCS > BLACKJACK){
                hit.isEnabled = false
                stand.isEnabled = false
                result.text = DUELRESLT[cmpScore(pCS,dCS, player)]
                openCard(dealerZone)
                //次
                nextSet()
                return@setOnClickListener
            }
        }
        ////////////////
        //今の持ち札で対戦
        stand.setOnClickListener{
            //これ以上のカード操作不可
            stand.isEnabled = false
            hit.isEnabled = false
            //ディーラがカードを引く
            drawCard(dealer,dealerZone,false)
            //結果
            val pCS = calcCardScore(hand,playerCS,true)
            val dCS = calcCardScore(dealer,dealerCS,false)
            result.text = DUELRESLT[cmpScore(pCS,dCS, player)]
            //次
            nextSet()
        }
        //////////////

        ////ヘルプ表示
        help.setOnClickListener{
            capZone.visibility = View.VISIBLE
            stand.visibility = View.GONE
            hit.visibility = View.GONE
//            moveTest.visibility = View.GONE
            help.visibility = View.GONE
        }
        mainZone.setOnClickListener{
            if(capZone.visibility == View.VISIBLE){
                capZone.visibility = View.GONE
                stand.visibility = View.VISIBLE
                hit.visibility = View.VISIBLE
                help.visibility = View.VISIBLE
//                moveTest.visibility = View.VISIBLE
            }
        }
    }
}
