package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.widget.TextView

class Deck {
    private val DECKPCS = 2 //デッキ生成数
    private val SUITNUM = 13 //1絵柄に対応するカードの数

//    val cards:MutableList<Trump> = makeDeck() //デッキ

    //トランプデッキの生成
    fun makeDeck() {
        cards.clear()
        for(i in 1..DECKPCS){
            for(i2 in 1..SUITNUM){
                cards.add(Trump("dia", i2))
                cards.add(Trump("heart", i2))
                cards.add(Trump("spade", i2))
                cards.add(Trump("club", i2))
            }
        }
        cards.shuffle()
    }
    //デッキ内カード存在チェック
    fun cardsRemaining():Boolean{
        return cards.size > 0
    }
    //引いたカードをデッキから削除
    fun remTrump(){
        if(cardsRemaining()) cards.remove(cards.first())
    }
    //トランプセットの残り枚数を更新する
    @SuppressLint("SetTextI18n")
    fun countCards(view: TextView){
        if(view.id == R.id.endsCards){
            view.text = "count:${cards.count()}"
        }
    }
}