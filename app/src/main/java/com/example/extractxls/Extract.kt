package com.example.extractxls

import java.io.*
import java.nio.charset.Charset


fun main() {
    try {
        val fileName = "animal_plant_3"

        val br = BufferedReader(
            (InputStreamReader(
                FileInputStream("/Users/bag-eunji/deskTop/${fileName}.csv"),
                Charset.forName("UTF-16")
            ))
        )

        val list = br.use(BufferedReader::readLines)
        br.close()

        val map = getMap(list) // Key - value
        val iterator = map.iterator()
        val finalMap = mutableMapOf<String, String>() //key 가 ㄱㅏㅁㅈㅏ 형식

        while (iterator.hasNext()) {
            val next = iterator.next()
            val separatedWord = Util().doSeparating(next.key)
            if (separatedWord.first) {
                finalMap[separatedWord.second!!] = next.value
            }
        }

        finalMap.forEach {
            println("mapKey ${it.key}")
        }

        writeCSV(finalMap, fileName)

    } catch (e: IOException) {
        e.printStackTrace();
    }
}

private fun getMap(list: List<String>): MutableMap<String, String> {
    val map = mutableMapOf<String, String>()
    list.map {
        val splitList = it.split(",", ignoreCase = false, limit = 2) //limit > 최대 두개 까지의 요소만 리턴
        map.put(splitList[0], splitList[1])
    }

    return map
}


private fun writeCSV(map: Map<String, String>, fileName: String) {
    try {
        val bw =
            BufferedWriter(FileWriter(("/Users/bag-eunji/deskTop/${fileName}_filtered.csv"), true))
        val iterator = map.iterator()
        while (iterator.hasNext()) {
            val word = iterator.next()
            bw.write("${word.key},${word.value}")
            bw.newLine()
            bw.flush()
        }
        bw.close()
    } catch (e: Exception) {
        println(e)
    }
}