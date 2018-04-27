package com.example.dell.blackjack

import android.view.View
import android.widget.LinearLayout
import android.content.Context
import org.jetbrains.anko.db.INTEGER


val trumps = mutableListOf<Trump>() //デッキ
val hand = mutableListOf<Hand>() //手札(プレイヤー)
val dealer = mutableListOf<Hand>() //手札(ディーラー)
val selCards = mutableListOf<View>() //(未使用) 選択したカード
val calcLi = mutableListOf<Hand>() //スコア計算用
val dpVs = mutableMapOf(PLAYER to 0, DEALER to 0) //1ゲームの結果
val status = mutableMapOf("winC" to 0,"loseC" to 0) //1セットの勝敗(使わないかも)
val player = Wager(0,0)

//val prefKey = listOf("chip")

/*
* プリファレンス関連
* 現状は、プリファレンスファイルが1つの想定
* 大規模なものを作るときは引数にプリファレンスファイル名を追加する
*/
/** 入力された「キー：数値」をプリファレンスに保存する、存在していれば上書きする */
fun setChip(context: Context, key: String, num: Int) {
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PREFILENAME, Context.MODE_PRIVATE)
    // プリファレンスに書き込むためのEditorオブジェクト取得 //
    val editor = pref.edit()
    editor.putInt(key, num)
    // 書き込みの確定（実際にファイルに書き込む）
    editor.commit()
}
/** プリファレンスから「キーに一致する値」を取り出す。登録されていなければ -1 を返す  */
fun loadChip(context: Context,key: String): Int {
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PREFILENAME, Context.MODE_PRIVATE)
    // "user_age" というキーで保存されている値を読み出す
    return pref.getInt(key, -1)
}
/** プリファレンスからキーを削除 */
fun removePref(context: Context,key:String){
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PREFILENAME, Context.MODE_PRIVATE)
    // "user_age" というキーで保存されている値を読み出す
    if(pref.getInt(key, -1) == -1){
        return
    }
    val editor = pref.edit()
    editor.remove(key)
    editor.commit()
}

/**
 * 山札
 * @suit 絵札
 * @num カード番号
 * @isFace 表裏 初回は裏
 * @id 手札処理用
 */
open class Trump(val suit: String, val num: Int)

/**
 * カード
 * @card カードを表示するView
 * @Trump カードの情報
 * @hidFlg 裏表
 *
 */
//class Hand(val card: View, val trump: Trump)
class Hand(val card: LinearLayout, val trump: Trump, var hidFlg: Boolean) {
    fun open(hidFlg: Boolean) {this.hidFlg = !hidFlg}
}

//ゲーム時のチップの流れ
class Wager(var ownChip: Int,var betChip: Int=0){
    fun setBet(bet:Int){this.betChip = bet}
    fun resetBet(){this.betChip = 0}

    fun startChip(){this.ownChip += 3000} //初期値 chipが100未満で出来るようにしたい
    fun addChip(){this.ownChip += 500}  //広告を見た時に加算とかしたい

    //勝負前チップ判定
    fun callChip():Boolean{
        return this.ownChip >= this.betChip
    }

    //勝負後のチップ処理
    fun resultChip(dividend:Double){
        val rst: Double = (this.ownChip-this.betChip) + (this.betChip*dividend)
        this.ownChip = rst.toInt()
    }
}

