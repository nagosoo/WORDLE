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
    private val sampleWord = arrayOf("ㄱ", "ㅜ", "ㅁ", "ㅓ", "ㅇ")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onClick(keyboard: View?) {
        val delete = binding.keyboard.delete.id
        val enter = binding.keyboard.enter.id

        //마지막 자모 일 때 엔터를 누르기 전까지 order을 +하지 않는다.
        if (order % 5 == 4) {
            (binding.gridLayout.gridLayout[order] as TextView).text =
                (keyboard as AppCompatButton).text
            binding.keyboard.enter.setOnClickListener {
                checkAnswer()
                order += 1
            }
            return@onClick
        }

        binding.keyboard.delete.setOnClickListener {
            if (order % 5 != 0) {
                (binding.gridLayout.gridLayout[order] as TextView).text = ""
                order -= 1
            } else {
                (binding.gridLayout.gridLayout[order] as TextView).text =
                    "" //첫 자모 일 때는 delete 눌러도 order - 하지 않는다.
            }
        }

        if (keyboard?.id != delete && keyboard?.id != enter && order in 0..24) {
            (binding.gridLayout.gridLayout[order] as TextView).text =
                (keyboard as AppCompatButton).text
            order += 1
        }
    }

    private fun checkAnswer() {
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