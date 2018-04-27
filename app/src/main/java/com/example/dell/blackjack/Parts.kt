@file:Suppress("UNREACHABLE_CODE")

package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.dell.blackjack.R.id.*
import org.jetbrains.anko.*


/*定数*/
////カードルール
private const val SUITNUM:Int = 13 //絵札ごとのカード数
private const val HANDNUM:Int = 2 //初回の手札の数
private const val DEALSTOPSCR:Int = 17 //ディーラーがこれ以上カードを引かなくなる数
private const val ACEHIGH:Int = 11 //ACEを高い値で数える
private const val ACELOW:Int = 1 //ACEを低い値で数える
private const val JQK:Int = 10 //絵札の値
const val BLACKJACK:Int = 21 //BLACKJACKの値
const val DECKCONT:Int = 2 //使用するデッキの数
////レイアウト
private const val CARDF = "#f5deb3"//カード表の色
private const val CARDB = "#d2b48c"//カード裏の色
private const val CARDSEL = "#deb887" //カード選択枠の色
private const val CARDW:Int = 45 //cardの横幅
private const val CARDH:Int = 60 //cardの縦幅

//汎用キー
const val PLAYER:String = "player" //操作側の名称
const val DEALER:String = "dealer" //メインNPCの名称

////プリファレンス関連
val PREFILENAME = "ownChip" //プリファレンスファイル名
val PLAYERMONEY = "chip" //操作側の所持金

////所持金関係
const val FIRSTCHIP:Int = 3000 //初回起動時チップ
const val DEBAGMANYCHIP:Int = 1000000 //初回起動時チップ

////Bet
const val BET1:Int = 500 //ベット1
const val BET2:Int = 1000 //ベット2
const val BET3:Int = 5000 //ベット3
const val LOSEOS:Double = 0.0
const val WINOS:Double = 3.0
const val BJOS:Double = 3.5
const val PUSHOS:Double = 2.0
val DUELRESLT = listOf<String>("BJWIN","WIN","LOSE","PUSH")

@SuppressLint("SetTextI18n")
//キャプションのセット
fun setCaption(capZone:FrameLayout){
    capZone.frameLayout{
        backgroundColor = Color.parseColor(CARDB)
        padding = dip(10)
        textView{
            text =
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
    }
}


//トランプセットの残り枚数を更新する
@SuppressLint("SetTextI18n")
fun countCards(view: TextView){
    if(view.id == endsCards){
        view.text = "count:${trumps.count()}"
    }
}
//引いたカードをデッキから削除
fun remTrump(){
    if(trumps.firstOrNull() != null) trumps.remove(trumps.first())
}
//トランプデッキの生成
fun makeDeck(count:Int) {
    trumps.clear()
    for(i in 1..count){
        for(i in 1..SUITNUM){
            trumps.add(Trump("dia", i))
            trumps.add(Trump("heart", i))
            trumps.add(Trump("spade", i))
            trumps.add(Trump("club", i))
        }
    }
    trumps.shuffle()
}

/**
 *初回の手札を引く
 * @userZone 手札を表示するView
 * @user 手札を格納する変数
 */
//fun makeHand(userZone: LinearLayout) {
fun makeHand(userZone: LinearLayout, user: MutableList<Hand>,playerFlg: Boolean) {
    user.clear()
    var score = 0
    for(i in 1..HANDNUM){
        //山札が0枚の時はデッキの再生成
        if(trumps.size == 0 ){
            makeDeck(DECKCONT)
        }
        if(trumps.firstOrNull() != null) {
            val trump = trumps.first()
            score += trump.num
            user += Hand(userZone.linearLayout {
                textView {
                    backgroundColor = Color.parseColor(CARDF)
                    text = "${trumps.first().suit}\n${trumps.first().num}"
                    if(i > 1 && playerFlg){
                        //2枚目以降ユーザー
//                        translationX = dip((userZone.childCount)*RAYERWID).toFloat()
                    }
                    if(i > 1 && !playerFlg){
                        //2枚目以降ディーラー
                        backgroundColor = Color.parseColor(CARDB)
                        text = ""
//                        translationX = dip((userZone.childCount)*RAYERWID).toFloat()
                    }
                    gravity = Gravity.LEFT
                }.lparams(width = userZone.width) {
                    width = dip(CARDW)
                    height = dip(CARDH)
                    gravity = Gravity.LEFT
                    horizontalMargin = dip(5)
                    verticalMargin = dip(5)
                }
            },trump,if(i > 1 && !playerFlg)true else false)
            remTrump()
        }else{
            break
        }
    }
    if(playerFlg){
        dpVs[PLAYER] = score
    }else{
        dpVs[DEALER] = score
    }

}

/**
 * カードを引く
 * @userZone 手札を表示するView
 */
@SuppressLint("SetTextI18n")
fun drawCard(user: MutableList<Hand>, userZone: LinearLayout, playerFlg: Boolean) {
    //山札が0枚の時はデッキの再生成
    if(trumps.size == 0 ){
        makeDeck(DECKCONT)
    }
   //ディーラーパターン(条件を満たすまでカードを引き続ける)
    if(!playerFlg){
        openCard(userZone)
        //3枚目以降の処理
        var dealerScore = calcpt(dealer,false)
        var playerscore = calcpt(hand,false)
        while(dealerScore<DEALSTOPSCR){
            //無駄に引く必要がなかったら止める
            if(playerscore<dealerScore){
                return
            }
            addCard(user,userZone)
            dealerScore = calcpt(dealer,false)
        }
    }else{
        //プレイヤーパターン(一度だけカードを引く)
        addCard(user,userZone)
    }
}
//裏返しのカードを返す
fun openCard(userZone:LinearLayout){
    //裏返しのカードを返す
    if(userZone.childCount == HANDNUM){
        //2枚目でhit
        for (d in dealer)
            if(d.hidFlg){
                d.card.linearLayout{
                    textView {
                        text = "${d.trump.suit}\n${d.trump.num}"
                        backgroundColor = Color.parseColor(CARDF)
//                             translationX = dip((userZone.childCount-1)*RAYERWID).toFloat()
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
                d.card.removeView(d.card.getChildAt(d.card.childCount-2))
//                    d.card.removeView(d.card.getChildAt(d.card.childCount))
                //test
                for(d in dealer) println("tfTest:${d.hidFlg}")
            }
    }
}
//手札にデッキの一番上の情報を記載したカードを追加する
fun addCard(user: MutableList<Hand>, userZone: LinearLayout){
    user += Hand(userZone.linearLayout{
        textView {
            text = "${trumps.first().suit}\n${trumps.first().num}"
            gravity = Gravity.LEFT
            backgroundColor = Color.parseColor(CARDF)
        }.lparams(width = userZone.width) {
            width = dip(CARDW)
            height = dip(CARDH)
            gravity = Gravity.LEFT
            horizontalMargin = dip(5)
            verticalMargin = dip(5)
//            translationX = dip((userZone.childCount)*RAYERWID).toFloat()
        }
    },trumps.first(),false)
    remTrump()
}


//スコアに対しての画面書き込みを行う
@SuppressLint("SetTextI18n")
fun calcCardScore(user: MutableList<Hand>, write: TextView,playerFlg: Boolean): Int {
    var cc = calcpt(user,false)
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
fun calcpt(user: MutableList<Hand>,firstFlg:Boolean): Int{
    var cc = 0
    var aceCount01 = 0
    var aceCount11 = 0
    //昇順
    calcLi.clear()
    calcLi.addAll(user)
    calcLi.sortBy { it.trump.num}

    //なんか複雑な判定とか計算
    for (card in calcLi){
        //裏返しのカードは計算しない
        if(card.hidFlg && !firstFlg) continue

        //絵札は10で統一
        if(card.trump.num > JQK){
            cc+= JQK
            continue
        }

        //とりあえずはplayerもdealerも同条件の加算(1の扱いを指定しない)
        if(card.trump.num == 1){
//            if(cc < ACEHIGH){
            if(cc <= (BLACKJACK-ACEHIGH)){
                cc+= ACEHIGH
                aceCount11++
                continue
            }else{
                cc++
                aceCount01++
                continue
            }
            //+ACELOWすると22以上かつ1度以上ACEHIGH(11)を利用している
            // (ACE(11),ACE(1)) -> (ACE(1),ACE(1))
            if(aceCount11>0 && cc> (BLACKJACK - ACEHIGH)){
                cc-=(ACEHIGH - ACELOW)//ace(11)->ace(1)
                cc++ //今回のace(1)
                aceCount01+=2
                aceCount11--
                continue
            }
        }
        //Aceありかつ今回のカードで22以上になる
        if(cc + card.trump.num > BLACKJACK && aceCount11>0){
            cc-= (ACEHIGH - ACELOW)
            cc+=card.trump.num
            aceCount11--
            aceCount01++
            continue
        }
        //その他
        cc+=card.trump.num
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
fun cmpScore(playerScr:Int,dealerScr:Int,rst:Wager) :Int{
    val BustFlgP = playerScr > BLACKJACK
    val BustFlgD = dealerScr > BLACKJACK
    val BJFlgP = playerScr == BLACKJACK
    val BJFlgD = dealerScr == BLACKJACK
    when {
        BustFlgP -> {
            //LOSE
            rst.resultChip(LOSEOS)
            return 2
        }
        BustFlgD -> {
            if(BJFlgP){
                //BJ WIN
                rst.resultChip(BJOS)
                return 0
            }
            //WIN
            rst.resultChip(WINOS)
            return 1
        }
        playerScr > dealerScr -> {
            if(BJFlgP){
                //BJ WIN
                rst.resultChip(BJOS)
                return 0
            }
            //WIN
            rst.resultChip(WINOS)
            return 1
        }
        playerScr < dealerScr ->{
            //LOSE
            rst.resultChip(LOSEOS)
            return 2
        }
        else ->
            //PUSH
            return 3
    }
}

/////////////////////////////////////////////////////////////////////////////
////以下未使用
/**
 * ※未使用※
 * 手札からカードを選択する
 * @throwCard 手札クラス
 * @field 選択したカードを表示するView
 * @handZone 手札を表示するView
 * @selCardView 選択したカードのView
 */
fun selCard(selCard: Hand, field: FrameLayout, handZone: LinearLayout, selCardView: View){
    for ((i,s) in selCards.withIndex())
        if(s == selCardView){
            selCardView.setBackgroundColor(0)
            selCards.drop(i)
            return
        }
    //選択状態
    selCardView.setBackgroundColor(Color.parseColor(CARDSEL))

    selCards.add(selCardView)
}

/**
 * ※未使用※
 * 裏面のカードをオープンする
 */
fun openCard(selHand:Hand,handZone: LinearLayout){
    val tText = "${selHand.trump.suit}\n${selHand.trump.num}"
    val cView = selHand.card
//    cView.setBackgroundColor(Color.parseColor(CARDF))
    println("test${cView.childCount}")
    cView.removeAllViews()
    println("test${cView.childCount}")
    handZone.linearLayout{
        textView {
            backgroundColor = Color.parseColor(CARDF)
            text = tText
            gravity = Gravity.LEFT
        }.lparams(width = handZone.width) {
            width = dip(CARDW)
            height = dip(CARDH)
            gravity = Gravity.LEFT
            horizontalMargin = dip(5)
            verticalMargin = dip(5)
        }
    }
}
