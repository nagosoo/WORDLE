package com.example.extractxls

import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.extractxls.Util.Companion.LOG
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val am = resources.assets
        try {
            val inputStream = am.open("food_1.csv", AssetManager.ACCESS_BUFFER)
            val br = BufferedReader((InputStreamReader(inputStream, Charset.forName("UTF-16"))))

            val list = br.use(BufferedReader::readLines)
            br.close()

            val map = getMap(list)
            val iterator = map.iterator()

            while (iterator.hasNext()){
                if(!Util().doSeparating(iterator.next().key)){
                    iterator.remove()
                }
            }

            map.forEach {
                Log.d(LOG,"mapKey ${it.key}")
            }




        } catch (e: IOException) {
            e.printStackTrace();
        }
    }

    private fun getMap(list: List<String>): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        list.map {
            val splitList = it.split(",")
            map.put(splitList[0], splitList[1])
        }

        return map
    }
}


