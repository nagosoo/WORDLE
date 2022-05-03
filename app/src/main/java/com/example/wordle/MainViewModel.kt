package com.example.wordle

import androidx.lifecycle.ViewModel
import com.example.wordle.dialog.ProgressDialog

class MainViewModel : ViewModel() {

    var globalFileName = ""
    var globalLevel = -1
    lateinit var questionWord: Array<String>
    lateinit var questionMeaning: String
}