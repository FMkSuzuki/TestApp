package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.widget.TextView

@Suppress("UNREACHABLE_CODE")
class Game {
    val PLAYER:String = "player" //操作側の名称
    val DEALER:String = "dealerHand" //メインNPCの名称
    val DEALSTOPSCR:Int = 17 //ディーラーがこれ以上カードを引かなくなる数
    val ACEHIGH:Int = 11 //ACEを高い値で数える
    val ACELOW:Int = 1 //ACEを低い値で数える
    val JQK:Int = 10 //絵札の値
    val BLACKJACK:Int = 21 //BLACKJACKの値

    val dpVs = mutableMapOf(PLAYER to 0, DEALER to 0) //1ゲームの結果

    //キャプションのセット
    @SuppressLint("SetTextI18n")
    fun setCaption(text: TextView){
        text.text =
                """
    |【RANK】
    | Ace :1or11
    | Jack,Queen,King:10
    | else:ownNumber
    |
    |【Rate】
    | Win(BJ):×2.5
    | Win:×2
    | PUSH:×1
    | LOSE:×0
""".trimMargin()
    }
    //スコアに対しての画面書き込みを行う
    @SuppressLint("SetTextI18n")
    fun editCardScore(user: MutableList<Trump.Hand>, write: TextView, playerFlg: Boolean): Int {
        val cc = calcScore(user,false)
        if(write.text.indexOf("Player") != -1){
            write.text = "Player:$cc"
            if(cc > BLACKJACK){
                write.text = "Player:$cc <Bust>"
            }else if(cc == BLACKJACK){
                write.text = "Player:$cc <BJ>"
            }
        }else{
            write.text = "Dealer:$cc"
            if(cc > BLACKJACK){
                write.text = "Dealer:$cc <Bust>"
            }else if(cc == BLACKJACK){
                write.text = "Dealer:$cc <BJ>"
            }
        }
        if(playerFlg){
            dpVs[PLAYER] = cc
        }else{
            dpVs[DEALER] = cc
        }
        return cc
    }
    //スコア計算
    fun calcScore(user: MutableList<Trump.Hand>, firstFlg:Boolean): Int{
        var cc = 0
        var aceCount01 = 0
        var aceCount11 = 0

        calcLi.clear()
        calcLi.addAll(user)
        calcLi.sortBy { it.num}

        //なんかいろんな判定とか計算する
        for (card in calcLi){
            //裏返しのカード初回以外計算しない
            if(card.hidFlg && !firstFlg) continue

            //絵札は10で統一
            if(card.num > JQK){
                cc+= JQK
                continue
            }

            //ACELOW ACEHIGH判定
            if(card.num == 1){
                if(cc <= (BLACKJACK-ACEHIGH)){
                    cc+= ACEHIGH
                    aceCount11++
                    continue
                }else{
                    cc++
                    aceCount01++
                    continue
                }
                //+ACELOWするとBustかつ1度以上ACEHIGHを利用している
                // (ACE(11),ACE(1)) -> (ACE(1),ACE(1))
                if(aceCount11>0 && cc> (BLACKJACK - ACEHIGH)){
                    cc-=(ACEHIGH - ACELOW)//ace(11)->ace(1)
                    cc++ //今回のace(1)
                    aceCount01+=2
                    aceCount11--
                    continue
                }
            }
            //ACEHIGHありかつ今回のカードでBustになる
            if(cc + card.num > BLACKJACK && aceCount11>0){
                cc-= (ACEHIGH - ACELOW)
                cc+=card.num
                aceCount11--
                aceCount01++
                continue
            }
            //その他(ACELOW加算)
            cc+=card.num
        }
        return cc
    }
    /**
     * 勝負判定
     * BJWIN 0
     * WIN 1
     * LOSE 2
     * PUSH 3
     */
    fun cmpScore(playerScr:Int,dealerScr:Int,rst:Chip) :Int{
        val BustFlgP = playerScr > BLACKJACK
        val BustFlgD = dealerScr > BLACKJACK
        val BJFlgP = playerScr == BLACKJACK
        when {
            BustFlgP -> {
                //LOSE
                rst.resultChip(rst.LOSEOS)
                return 2
            }
            BustFlgD -> {
                if(BJFlgP){
                    //BJ WIN
                    rst.resultChip(rst.BJOS)
                    return 0
                }
                //WIN
                rst.resultChip(rst.WINOS)
                return 1
            }
            playerScr > dealerScr -> {
                if(BJFlgP){
                    //BJ WIN
                    rst.resultChip(rst.BJOS)
                    return 0
                }
                //WIN
                rst.resultChip(rst.WINOS)
                return 1
            }
            playerScr < dealerScr ->{
                //LOSE
                rst.resultChip(rst.LOSEOS)
                return 2
            }
            else ->
                //PUSH
                return 3
        }
    }
}