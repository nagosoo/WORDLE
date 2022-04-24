package com.example.wordle

import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.random.Random

fun getWord(
    am: AssetManager,
    fileName: String,
    level: Int
): MutableMap.MutableEntry<Array<String>, String>? {
    return try {
        val inputStream = am.open("${fileName}_${level}_filtered.csv", AssetManager.ACCESS_BUFFER)
        val br = BufferedReader((InputStreamReader(inputStream, Charset.forName("UTF-8"))))
        val list = br.use(BufferedReader::readLines)
        br.close()

        val map = getMap(list)
        val randomNum = Random.nextInt(0, map.size)
        map.entries.elementAt(randomNum)

    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun getMap(list: List<String>): MutableMap<Array<String>, String> {
    val map = mutableMapOf<Array<String>, String>()
    list.map {
        val jamoKey = it.substring(0, 5) //ㄱㅏㅁㅈㅏ
        val meaningString = it.substringAfterLast("[").substringBefore("]") //뜻만 parsing
        val parsedMeaning = meaningString.replace("[^가-힣.\\s]".toRegex(), "") //따옴표 쉼표 등 제외 한글&.만 추출
        val meaningList = parsedMeaning.split(".").filter { it.isNullOrEmpty().not() } //마지막 뜻에 있는 . 뒤에 Parsing X
        val finalMeaning = meaningList.withIndex().joinToString(separator = "", transform = {
            if (it.index == meaningList.lastIndex) {
                "${it.index + 1}. ${it.value.trim()}"
            } else {
                "${it.index + 1}. ${it.value.trim()}\n"
            }
        })

        val listKey = mutableListOf<String>()
        for (element in jamoKey) {
            listKey.add(element.toString())
        }

        map.put(listKey.toTypedArray(), finalMeaning)
    }

    return map
}
