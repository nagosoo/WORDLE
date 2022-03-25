package com.example.wordle

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.wordle.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var order = 0
    private lateinit var binding: ActivityMainBinding
    private var isLastLetter = false
    private val orange by lazy { ContextCompat.getColor(this, R.color.semi_correct_orange) }
    private val green by lazy { ContextCompat.getColor(this, R.color.correct_green) }
    private val gray by lazy { ContextCompat.getColor(this, R.color.incorrect_gray) }
    private val sampleWord = arrayOf("ㄱ", "ㅜ", "ㄱ", "ㅓ", "ㅇ")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClickListener()

    }

    private fun setClickListener() {
        binding.keyboard.enter.setOnClickListener {
            if (isLastLetter) {
                checkAnswer()
                isLastLetter = false
            } else {
                Toast.makeText(this, "덜입력", Toast.LENGTH_SHORT).show()
            }
        }

        binding.keyboard.delete.setOnClickListener {
            if (isLastLetter || order % 5 != 0) {
                if (order % 5 == 0) isLastLetter = false
                order -= 1
                (binding.gridLayout.gridLayout[order] as TextView).text = ""
            }
        }
    }

    override fun onClick(keyboard: View?) {
        if (isLastLetter) return
        if (order in 0..24) {
            (binding.gridLayout.gridLayout[order] as TextView).text =
                (keyboard as AppCompatButton).text
            order += 1
            if (order % 5 == 0) isLastLetter = true
        }
    }

    private fun checkAnswer() {
        val first = (binding.gridLayout.gridLayout[order - 5] as TextView).text
        val second = (binding.gridLayout.gridLayout[order - 4] as TextView).text
        val third = (binding.gridLayout.gridLayout[order - 3] as TextView).text
        val fourth = (binding.gridLayout.gridLayout[order - 2] as TextView).text
        val fifth = (binding.gridLayout.gridLayout[order - 1] as TextView).text

        val answerArray = arrayOf(first, second, third, fourth, fifth)

        if (checkAllCorrect(answerArray)) {
            for (i in 1..5) {
                binding.gridLayout.gridLayout[order - i].setBackgroundColor(green)
            }
            Toast.makeText(this, "다맞음", Toast.LENGTH_SHORT).show()
            return
        }
        checkSemiCorrect(answerArray)
    }

    private fun checkAllCorrect(answerArray: Array<CharSequence>): Boolean {
        return answerArray.contentEquals(sampleWord)
    }

    private fun checkSemiCorrect(answerArray: Array<CharSequence>) {
        for (i in 0..4) {
            if (answerArray[i] == sampleWord[i]) {
                setGridViewColor(i, green)
                setKeyboardColor(i, green)
            } else if (answerArray[i] != sampleWord[i] && checkContainLetter(answerArray[i])) {
                setGridViewColor(i, orange)
                setKeyboardColor(i, orange)
            } else {
                setGridViewColor(i, gray)
                setKeyboardColor(i, gray)
            }
        }
    }

    private fun checkContainLetter(letter: CharSequence): Boolean {
        return sampleWord.contains(letter)
    }

    private fun setGridViewColor(i: Int, color: Int) {
        binding.gridLayout.gridLayout[i + (order - 5)].setBackgroundColor(color)
    }

    private fun setKeyboardColor(i: Int, color: Int) {
        val uniCode = (binding.gridLayout.gridLayout[i + (order - 5)] as TextView).text[0].code
        val keyboard = binding.keyboard.tableLayout.findViewWithTag<AppCompatButton>("$uniCode")
        keyboard.setBackgroundColor(color)
    }

}