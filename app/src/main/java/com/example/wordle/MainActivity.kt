package com.example.wordle

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.get
import com.example.wordle.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var order = 0
    private lateinit var binding: ActivityMainBinding
    private var isLastLetter = false
    private val sampleWord = arrayOf("ㄱ", "ㅜ", "ㅁ", "ㅓ", "ㅇ")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClickListener()
    }

    private fun setClickListener() {
        binding.keyboard.enter.setOnClickListener {
            if (order % 5 == 0) {
                checkAnswer()
            } else {Toast.makeText(this,"덜입력",Toast.LENGTH_SHORT).show()}
        }

        binding.keyboard.delete.setOnClickListener {
            if (isLastLetter || order % 5 != 0) {
                order -= 1
                (binding.gridLayout.gridLayout[order] as TextView).text = ""
                if (order % 5 == 0) isLastLetter = false
            }
        }
    }

    override fun onClick(keyboard: View?) {
        if (order in 0..24) {
            (binding.gridLayout.gridLayout[order] as TextView).text =
                (keyboard as AppCompatButton).text
            order += 1
            if (order % 5 == 0) isLastLetter = true
        }
    }

    private fun checkAnswer() {
        Toast.makeText(this, "entered ${order}", Toast.LENGTH_SHORT).show()
        //정답이 맞는지 체크한다.
        val first = (binding.gridLayout.gridLayout[order - 4] as TextView).text
        val second = (binding.gridLayout.gridLayout[order - 3] as TextView).text
        val third = (binding.gridLayout.gridLayout[order - 2] as TextView).text
        val fourth = (binding.gridLayout.gridLayout[order - 1] as TextView).text
        val fifth = (binding.gridLayout.gridLayout[order] as TextView).text

        val answerArray = arrayOf(first, second, third, fourth, fifth)

        if (checkAllCorrect(answerArray)) {
            Toast.makeText(this, "다맞음", Toast.LENGTH_SHORT).show()
            return@checkAnswer
        }
    }

    private fun checkAllCorrect(answerArray: Array<CharSequence>): Boolean {
        return answerArray.contentEquals(sampleWord)
    }

    private fun checkSemiCorrect(answerArray: Array<CharSequence>) {
        for (i in 0..4) {
            if (answerArray[i] != sampleWord[i] && checkContainLetter(answerArray[i])) {
                //gridlayout[  i + (order-4)] backgoround 컬러 바꾸기
            }
            if (answerArray[i] != sampleWord[i] && !checkContainLetter(answerArray[i])) {
            }
        }
    }

    private fun checkContainLetter(letter: CharSequence): Boolean {
        return sampleWord.contains(letter)
    }

}