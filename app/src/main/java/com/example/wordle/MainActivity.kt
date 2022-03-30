package com.example.wordle

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.wordle.MyApplication.Companion.uiOptions
import com.example.wordle.databinding.ActivityMainBinding
import com.example.wordle.dialog.DialogManual
import com.example.wordle.dialog.DialogStatistics
import com.example.wordle.status.CategoryType
import com.example.wordle.status.LevelType


class MainActivity : AppCompatActivity(), View.OnClickListener, HideBottomBar {
    private var order = -1
    private val sp by lazy { this.getPreferences(Context.MODE_PRIVATE) }
    private val am by lazy { resources.assets }
    private lateinit var binding: ActivityMainBinding
    private var isLastLetter = false
    private val orange by lazy { ContextCompat.getColor(this, R.color.semi_correct_orange) }
    private val green by lazy { ContextCompat.getColor(this, R.color.correct_green) }
    private val gray by lazy { ContextCompat.getColor(this, R.color.incorrect_gray) }

    private var globalFileName = ""
    private var globalLevel = -1
    private lateinit var questionWord: Array<String>
    private val questionMeaning = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClickListener()
        setSpinner(binding.spinnerCategory, R.array.category, ::setOnCategoryClickListener)
        setSpinner(binding.spinnerLevel, R.array.level, ::setOnLevelClickListener)
        // hideBottomBar()
    }

    private fun setSpinner(
        spinner: AppCompatSpinner,
        textArrayResId: Int,
        itemClickListener: (String) -> (Unit)
    ) {
        ArrayAdapter.createFromResource(
            this,
            textArrayResId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.selectedItem.toString()
                    itemClickListener(selectedItem)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    private fun setOnCategoryClickListener(category: String) {
        globalFileName = when (category) {
            CategoryType.ANIMAL_PLANT.category -> {
                CategoryType.ANIMAL_PLANT.fileName
            }
            CategoryType.FOOD.category -> {
                CategoryType.FOOD.fileName
            }
            else -> ""
        }
    }

    private fun setOnLevelClickListener(level: String) {
        globalLevel = when (level) {
            LevelType.LEVEL_1.levelString -> {
                LevelType.LEVEL_1.levelInt
            }
            LevelType.LEVEL_2.levelString -> {
                LevelType.LEVEL_2.levelInt
            }
            LevelType.LEVEL_3.levelString -> {
                LevelType.LEVEL_3.levelInt
            }
            else -> -1
        }
    }


    override fun hideBottomBar() {
        if (Build.VERSION.SDK_INT < 30) {
            window.decorView.systemUiVisibility = uiOptions
        } else {
            window.decorView.windowInsetsController!!.hide(WindowInsets.Type.statusBars())
        }
    }

    private fun setClickListener() {
        binding.keyboard.enter.setOnClickListener {
            if (isLastLetter) {
                checkAnswer()
            } else if (order in 0..24) {
                Toast.makeText(this, "글자 수를 덜 입력했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.keyboard.delete.setOnClickListener {
            if (isLastLetter || order % 5 != 0) {
                if (isLastLetter) isLastLetter = false
                order -= 1
                (binding.gridLayout.gridLayout[order] as TextView).text = ""
            }
        }
        binding.imageManual.setOnClickListener {
            DialogManual.Builder(this).build().show()
        }

        binding.imageStatistics.setOnClickListener {
            DialogStatistics.Builder(
                context = this,
                sp = sp
            ).build().show()
        }

        binding.buttonStart.setOnClickListener {
            if (globalFileName.isEmpty() || globalLevel < 1) {
                Toast.makeText(this, "카테고리와 난이도를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val word = getWord(am, globalFileName, globalLevel) //key - [value1, value2]
            word?.let {
                questionWord = it.key
                questionMeaning.clear()
                questionMeaning.addAll(it.value)

                for (element in questionWord) {
                    Log.d("LOGGING", element)
                }
                for (element in questionMeaning) {
                    Log.d("LOGGING", element)
                }
            }
            order = 0
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
        isLastLetter = false

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
            setSuccessPreference(order / 5)
            showStatisticsDialog()
            return
        }
        checkSemiCorrect(answerArray)

        //마지막까지 못 맞췄을 경우
        if (order == 25) {
            setFailPreference()
            showStatisticsDialog()
        }
    }

    private fun checkAllCorrect(answerArray: Array<CharSequence>): Boolean {
        return answerArray.contentEquals(questionWord)
    }

    private fun checkSemiCorrect(answerArray: Array<CharSequence>) {
        for (i in 0..4) {
            if (answerArray[i] == questionWord[i]) {
                setGridViewColor(i, green)
                setKeyboardColor(i, green)
            } else if (answerArray[i] != questionWord[i] && checkContainLetter(answerArray[i])) {
                setGridViewColor(i, orange)
                setKeyboardColor(i, orange)
            } else {
                setGridViewColor(i, gray)
                setKeyboardColor(i, gray)
            }
        }
    }

    private fun checkContainLetter(letter: CharSequence): Boolean {
        return questionWord.contains(letter)
    }

    private fun setGridViewColor(i: Int, color: Int) {
        binding.gridLayout.gridLayout[i + (order - 5)].setBackgroundColor(color)
    }

    private fun setKeyboardColor(i: Int, color: Int) {
        val uniCode = (binding.gridLayout.gridLayout[i + (order - 5)] as TextView).text[0].code
        val keyboard = binding.keyboard.tableLayout.findViewWithTag<AppCompatButton>("$uniCode")
        keyboard.setBackgroundColor(color)
    }

    private fun showStatisticsDialog() {
        order = -1

        val parsedMeaning = ""
        val iterator = questionMeaning.iterator()

        while(iterator.hasNext()){
            var cnt = 1
            val next = iterator.next()
            val meaning = next.replace("\"", "")
            parsedMeaning.plus("$cnt.$meaning\n")
            cnt+=1
        }

        Log.d("LOGGING", parsedMeaning)

        DialogStatistics.Builder(
            context = this,
            sp = sp,
            positiveButtonClickListener = ::setOnPositiveButtonClickListener,
            word = questionWord.joinToString(),
            meaning = parsedMeaning
        ).build()
            .show()
    }

    private fun setOnPositiveButtonClickListener() {
        finish()
        startActivity(intent)
    }

    private fun setSuccessPreference(successAtRow: Int) {
        val totalTry = sp.getInt("totalTry", 0)
        val key = "successAt$successAtRow"
        val successAtRowNum = sp.getInt(key, 0)
        val successiveSuccessNum = sp.getInt("successiveSuccess", 0)

        with(sp.edit()) {
            //총시도
            this.putInt("totalTry", totalTry + 1)
            //현재 연속 성공
            this.putInt("successiveSuccess", successiveSuccessNum + 1)
            //몇번째에?
            this.putInt("successAt$successAtRow", successAtRowNum + 1)
            apply()
        }
    }

    private fun setFailPreference() {
        val totalTry = sp.getInt("totalTry", 0)
        val successiveSuccessNum = sp.getInt("successiveSuccess", 0)
        val successiveSuccessArray = sp.getStringSet("successiveSuccessArray", setOf<String>())

        with(sp.edit()) {
            this.putInt("totalTry", totalTry + 1)
            this.putInt("successiveSuccess", 0) //현재 연속 성공 Reset

            successiveSuccessArray.isNullOrEmpty().not().let {
                val set = if (it) {
                    successiveSuccessArray!!.add(successiveSuccessNum.toString())
                    successiveSuccessArray.toSet()
                } else setOf(successiveSuccessNum.toString())
                this.putStringSet("successiveSuccessArray", set)
            }
            apply()
        }
    }
}