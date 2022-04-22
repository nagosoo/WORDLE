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
): MutableMap.MutableEntry<Array<String>, MutableList<String>>? {
    return try {
        val inputStream = am.open("${fileName}_${level}_filtered.csv", AssetManager.ACCESS_BUFFER)
        val br = BufferedReader((InputStreamReader(inputStream, Charset.forName("UTF-8"))))
        val list = br.use(BufferedReader::readLines)
        br.close()

        val map = getMap(list)
        val groupedList = map.toList().groupByTo(HashMap(), { it.first }, { it.second })
        val randomNum = Random.nextInt(0, groupedList.size)
        groupedList.entries.elementAt(randomNum)

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
        meaningString.replace("[]".toRegex(), "") //쌍따옴표, 쉼표 제거
        val meaningList = meaningString.split(".").filter { it.isNullOrEmpty().not() } //마지막 뜻에 있는 . 뒤에 Parsing X
        val finalMeaning = meaningList.withIndex().joinToString(separator = "", transform = {
            if (it.index == meaningList.lastIndex) {
                "${it.index + 1}. ${it.value}"
            } else {
                "${it.index + 1}. ${it.value}\n"
            }
        })
        //val splitList = it.split(",", ignoreCase = false, limit = 2) //limit > 최대 두개 까지의 요소만 리턴

        val listKey = mutableListOf<String>()
        for (element in jamoKey) {
            listKey.add(element.toString())
        }

        map.put(listKey.toTypedArray(), finalMeaning)
    }

    return map
}
