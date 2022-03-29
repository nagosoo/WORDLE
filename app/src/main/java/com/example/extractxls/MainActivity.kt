package com.example.extractxls

import android.content.res.AssetManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val am = resources.assets
        var inputStream: InputStream? = null
        try {
            inputStream = am.open("food_1.csv", AssetManager.ACCESS_BUFFER)

            val br = BufferedReader((InputStreamReader(inputStream, Charset.forName("UTF-16"))))

            val at = br.use(BufferedReader::readText)
            val tv = findViewById<TextView>(R.id.tv)
            tv.text = at

            br.close()

        } catch (e: IOException) {
            e.printStackTrace();
        }
    }
}