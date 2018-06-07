package com.example.dell.blackjack

import android.widget.LinearLayout

/**
 * カード情報
 * @suit 絵札
 * @num カード番号
 */
open class Trump(val suit: String,val num: Int){
    /**
     * カード情報(手札)
     * @location 手札を表示するView
     * @hidFlg 裏表
     */
    class Hand(val location: LinearLayout, suit: String, num: Int, var hidFlg: Boolean):Trump(suit,num){
        //カードを表にする
        fun open(hidFlg: Boolean) {this.hidFlg = !hidFlg}
    }
}