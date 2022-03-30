package com.example.extractxls

class Util {
    companion object{
       const val LOG = "LOGDATA"

        val CHO  = listOf( "ㄱ", "ㄱ ㄱ", "ㄴ", "ㄷ", "ㄷ ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅂ ㅂ", "ㅅ", "ㅅ ㅅ", "ㅇ", "ㅈ", "ㅈ ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" )
        val JOONG  = listOf( "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅗ ㅏ", "ㅗ ㅐ", "ㅗ ㅣ", "ㅛ", "ㅜ", "ㅜ ㅓ", "ㅜ ㅔ", "ㅜ ㅣ", "ㅠ", "ㅡ", "ㅡ ㅣ", "ㅣ"  )
        val JONG  = listOf( "", "ㄱ", "ㄱ ㄱ", "ㄱ ㅅ", "ㄴ", "ㄴ ㅈ", "ㄴ ㅎ", "ㄷ", "ㄹ", "ㄹ ㄱ", "ㄹ ㅁ", "ㄹ ㅂ", "ㄹ ㅅ", "ㄹ ㅌ", "ㄹ ㅍ", "ㄹ ㅎ", "ㅁ", "ㅂ", "ㅂ ㅅ", "ㅅ", "ㅅ ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" )

    }

    fun doSeparating(word: String): Boolean {

        var separatedWord = ""

        for (i in word.indices) {
            val uniVal = word[i].code - 0xAC00
            val cho = uniVal / 588
            val joong = uniVal % 588 / 28
            val jong = uniVal % 28
            //   Log.d(LOG, "${word[i]} => ${CHO[cho]} ${JOONG[joong]} ${JONG[jong]}")
            separatedWord += "${CHO[cho]} ${JOONG[joong]} ${JONG[jong]} "
            if (CHO[cho].length >= 2) return false //쌍자음 제외
            else if (JONG[jong].length >= 2) return false //이중받침 제외
            else if (joong == 3 || joong == 7) return false //쌍모음 제외
        }

        return separatedWord.replace(" ", "").length == 5
    }
}