package com.example.dell.blackjack
//未整理
/*定数*/
val player = Chip(0)//チップ、ベット操作
val deck = Deck()//山札操作
val fieldCards = FieldCards()//各自手札操作
val game = Game()//ゲーム処理系
val cards=mutableListOf<Trump>()//山札
var myHand = mutableListOf<Trump.Hand>() //手札(プレイヤー)
var dealerHand= mutableListOf<Trump.Hand>() //手札(ディーラー)
var calcLi = mutableListOf<Trump.Hand>() //スコア計算用
//data class HandCards(val myHand:MutableList<Hand>)