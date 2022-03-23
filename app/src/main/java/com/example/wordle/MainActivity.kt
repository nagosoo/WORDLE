package com.example.wordle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.retrofit.RestClient
import com.example.wordle.retrofit.RetrofitService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch {RestClient().service.getTestWords() }



    }
}