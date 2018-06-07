package com.example.dell.blackjack

import android.content.Context

class Chip(var ownChip: Int,var betChip: Int=0) {
    ////所持金関係
    val FIRSTCHIP:Int = 3000 //初回起動時チップ
    val DEBAGMANYCHIP:Int = 1000000 //テスト用
    ////Chip
    val BET1:Int = 500 //ベット1
    val BET2:Int = 1000 //ベット2
    val BET3:Int = 5000 //ベット3
    val LOSEOS:Double = 0.0 //敗北時配当
    val WINOS:Double = 3.0 //勝利時配当
    val BJOS:Double = 3.5 //BJ勝利時配当
    ////プリファレンス関連
    val PREFILENAME = "ownChip" //プリファレンスファイル名
    val PLAYERMONEY = "chip" //操作側の所持金

    val DUELRESLT = listOf("BJWIN","WIN","LOSE","PUSH") //画面書き込み用
    //ゲーム時のチップの流れ
    fun setBet(bet:Int){this.betChip = bet}
    fun resetBet(){this.betChip = 0}
    //勝負前チップ判定
    fun callChip():Boolean{
        return this.ownChip >= this.betChip
    }
    //勝負後のチップ処理
    fun resultChip(dividend:Double){
        val rst: Double = (this.ownChip-this.betChip) + (this.betChip*dividend)
        this.ownChip = rst.toInt()
    }


    /*
    * プリファレンス関連
    * 現状は、プリファレンスファイルが1つの想定
    * 大規模なものを作るときは引数にプリファレンスファイル名を追加する
    */
    /** 入力された「key：数値」をプリファレンスに保存する、存在していれば上書きする */
    fun setChip(context: Context,key: String,num: Int) {
        // プリファレンスの準備 //
        val pref = context.getSharedPreferences(PREFILENAME, Context.MODE_PRIVATE)
        // プリファレンスに書き込むためのEditorオブジェクト取得 //
        val editor = pref.edit()
        editor.putInt(key, num)
        // 書き込みの確定（実際にファイルに書き込む）
        editor.commit()
    }
    /** プリファレンスから「キーに一致する値」を取り出す。登録されていなければ -1 を返す  */
    fun loadChip(context: Context, key: String): Int {
        // プリファレンスの準備 //
        val pref = context.getSharedPreferences(PREFILENAME, Context.MODE_PRIVATE)
        // "user_age" というキーで保存されている値を読み出す
        return pref.getInt(key, -1)
    }
    /** プリファレンスからキーを削除 */
    fun removePref(context: Context, key:String){
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
}