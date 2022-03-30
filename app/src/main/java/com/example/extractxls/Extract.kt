package com.example.extractxls

import java.io.*
import java.nio.charset.Charset


fun main() {
    try {
        val fileName = "food_3"

        val br = BufferedReader(
            (InputStreamReader(
                FileInputStream("/Users/bag-eunji/deskTop/${fileName}.csv"),
                Charset.forName("UTF-16")
            ))
        )

        val list = br.use(BufferedReader::readLines)
        br.close()

        val map = getMap(list)
        val iterator = map.iterator()

        while (iterator.hasNext()) {
            if (!Util().doSeparating(iterator.next().key)) {
                iterator.remove()
            }
        }

        map.forEach {
            println("mapKey ${it.key}")
        }

        writeCSV(map, fileName)

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