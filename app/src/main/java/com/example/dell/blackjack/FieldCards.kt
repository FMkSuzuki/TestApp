package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import org.jetbrains.anko.*

class FieldCards{
    val CARDF = "#f5deb3"//カード表の色
    val CARDB = "#d2b48c"//カード裏の色
    val CARDW:Int = 45 //cardの横幅
    val CARDH:Int = 60 //cardの縦幅
    private val HANDNUM:Int = 2 //初回手札数

    /**
     *初回の手札を引く
     * @userZone 手札を表示するView
     * @user 手札を格納する変数
     */
    @SuppressLint("SetTextI18n", "RtlHardcoded")
    fun makeHand(userZone: LinearLayout, user: MutableList<Trump.Hand>, playerFlg: Boolean){
        println("\n\n\n\nuser1:${user.size}\n\n\n\n\n")
        user.clear()
        var score = 0
        for(i in 1..HANDNUM){
            //山札が0枚の時はデッキの再生成
            if(!deck.cardsRemaining()){
                deck.makeDeck()
            }else{
                val trump = cards.first()
                score += trump.num
                println("\n\n\n\nscore:${score}\n\n\n\n\n")
                user += Trump.Hand(userZone.linearLayout {
                    textView {
                        backgroundColor = Color.parseColor(CARDF)
                        text = "${trump.suit}\n${trump.num}"
                        if (i > 1 && !playerFlg) {
                            //2枚目以降ディーラー
                            backgroundColor = Color.parseColor(CARDB)
                            text = ""
                        }
                        gravity = Gravity.LEFT
                    }.lparams(width = userZone.width) {
                        width = dip(CARDW)
                        height = dip(CARDH)
                        gravity = Gravity.LEFT
                        horizontalMargin = dip(5)
                        verticalMargin = dip(5)
                    }
                }, trump.suit,trump.num, i > 1 && !playerFlg)
                deck.remTrump()
            }
        }
        println("\n\n\n\nuser2:${user.size}\n\n\n\n\n")
        println("\n\n\n\ndeck:${cards.size}\n\n\n\n\n")
        if(playerFlg){
            game.dpVs[game.PLAYER] = score
//            myHand = user
        }else{
            game.dpVs[game.DEALER] = score
//            dealerHand = user
        }
    }

    /**
     * カードを引く
     * @userZone 手札を表示するView
     */
    @SuppressLint("SetTextI18n")
    fun drawCard(user: MutableList<Trump.Hand>, userZone: LinearLayout, playerFlg: Boolean) {
        //山札が0枚の時はデッキの再生成
        if(!deck.cardsRemaining()){
            deck.makeDeck()
        }
        if(!playerFlg){
            //ディーラーパターン(条件を満たすまでカードを引き続ける)
            openCard(userZone)
            val playerscore = game.calcScore(myHand,false)
            var dealerScore = game.calcScore(dealerHand,false)
            while(dealerScore<game.DEALSTOPSCR){
                //無駄に引く必要がなかったら止める
                if(playerscore<dealerScore){
                    return
                }
                addCard(user,userZone)
                dealerScore = game.calcScore(dealerHand,false)
            }
        }else{
            //プレイヤーパターン(一度だけカードを引く)
            addCard(user,userZone)
        }
    }
    //手札にデッキの一番上の情報を記載したカードを追加する
    @SuppressLint("SetTextI18n", "RtlHardcoded")
    private fun addCard(user: MutableList<Trump.Hand>, userZone: LinearLayout){
        val tp = cards.first()
        user += Trump.Hand(userZone.linearLayout {
            textView {
                text = "${tp.suit}\n${tp.num}"
                gravity = Gravity.LEFT
                backgroundColor = Color.parseColor(CARDF)
            }.lparams(width = userZone.width) {
                width = dip(CARDW)
                height = dip(CARDH)
                gravity = Gravity.LEFT
                horizontalMargin = dip(5)
                verticalMargin = dip(5)
            }
        }, tp.suit,tp.num, false)
        deck.remTrump()
    }

    //裏返しのカードを返す
    @SuppressLint("SetTextI18n", "RtlHardcoded")
    fun openCard(userZone: LinearLayout){
        if(userZone.childCount != HANDNUM){
            return
        }
        for (d in dealerHand) {
            if(!d.hidFlg){
                continue
            }
            d.location.linearLayout {
                textView {
                    text = "${d.suit}\n${d.num}"
                    backgroundColor = Color.parseColor(CARDF)
                }.lparams(width = userZone.width) {
                    width = dip(CARDW)
                    height = dip(CARDH)
                    gravity = Gravity.LEFT
                    horizontalMargin = dip(5)
                    verticalMargin = dip(5)
                }
            }
            //カードをオープン状態にする(スコアに含める)
            d.open(d.hidFlg)
            //裏のカードとして使用していた空のテキストビューを削除するindex2: 配列:0~3 count:3なので裏は配列:1(count-2)
            d.location.removeView(d.location.getChildAt(d.location.childCount - 2))
        }
    }

}