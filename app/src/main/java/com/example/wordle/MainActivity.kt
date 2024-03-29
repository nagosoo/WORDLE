package com.example.wordle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.wordle.databinding.ActivityMainBinding
import com.example.wordle.dialog.DialogManual
import com.example.wordle.dialog.DialogStatistics
import com.example.wordle.dialog.ProgressDialog
import com.example.wordle.status.CategoryType
import com.example.wordle.status.LevelType


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var order = -1
    private val sp by lazy { this.getPreferences(Context.MODE_PRIVATE) }
    private lateinit var binding: ActivityMainBinding
    private var isLastLetter = false
    private val orange by lazy { ContextCompat.getColor(this, R.color.semi_correct_orange) }
    private val green by lazy { ContextCompat.getColor(this, R.color.correct_green) }
    private val gray by lazy { ContextCompat.getColor(this, R.color.incorrect_gray) }
    private val viewModel: MainViewModel by viewModels()
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClickListener()
        setSpinner(binding.spinnerCategory, R.array.category, ::setOnCategoryClickListener)
        setSpinner(binding.spinnerLevel, R.array.level, ::setOnLevelClickListener)

        showManualDialog()
    }

    private fun showManualDialog() {
        val isFirstTime = sp.getBoolean("isFirst", false)
        if (!isFirstTime) {
            DialogManual.Builder(this).build().show()
            with(sp.edit()) {
                this.putBoolean("isFirst", true)
                apply()
            }
        }
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
        viewModel.globalFileName = when (category) {
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
        viewModel.globalLevel = when (level) {
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

    private fun setClickListener() {
        binding.keyboard.enter.setOnClickListener {
            if (isLastLetter) {
                checkAnswer()
            } else if (order in 0..24) {
                Toast.makeText(this, "글자 수를 덜 입력했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.keyboard.delete.setOnClickListener {
            if (isLastLetter || order % 5 > 0) {
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
            progressDialog = ProgressDialog(this)
            progressDialog.show()

            if (viewModel.globalFileName.isEmpty() || viewModel.globalLevel < 1) {
                Toast.makeText(this, "카테고리와 난이도를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val word = getWord(resources.assets, viewModel.globalFileName, viewModel.globalLevel)
            word?.let {
                viewModel.questionWord = it.key
                viewModel.questionMeaning = it.value

                for (element in viewModel.questionWord) {
                    Log.d("LOGGING", element)
                }
                Log.d("LOGGING", viewModel.questionMeaning)

            }
            order = 0

            with(it as? AppCompatButton) {
                this?.text = "진행중"
                this?.isClickable = false
            }

            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
            }, 300)
        }

    }

    override fun onClick(keyboard: View?) {
        if (isLastLetter) return
        if (order in 0..24) {
            (binding.gridLayout.gridLayout[order] as TextView).text = (keyboard as AppCompatButton).text
            val aniBounce = AnimationUtils.loadAnimation(this, R.anim.scale_animation)
            binding.gridLayout.gridLayout[order].startAnimation(aniBounce)
            order += 1
            if (order % 5 == 0) isLastLetter = true
        }
    }

    private fun checkAnswer() {
        isLastLetter = false

        val first = getInputText(order - 5)
        val second = getInputText(order - 4)
        val third = getInputText(order - 3)
        val fourth = getInputText(order - 2)
        val fifth = getInputText(order - 1)

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

    private fun getInputText(order: Int): CharSequence {
        return ((binding.gridLayout.gridLayout[order] as TextView).text)
    }

    private fun checkAllCorrect(answerArray: Array<CharSequence>): Boolean {
        return answerArray.contentEquals(viewModel.questionWord)
    }

    private fun checkSemiCorrect(answerArray: Array<CharSequence>) {
        for (i in 0..4) {
            if (answerArray[i] == viewModel.questionWord[i]) {
                setGridViewColor(i, green)
                setKeyboardColor(i, green)
            } else if (answerArray[i] != viewModel.questionWord[i] && checkContainLetter(answerArray[i])) {
                setGridViewColor(i, orange)
                setKeyboardColor(i, orange)
            } else {
                setGridViewColor(i, gray)
                setKeyboardColor(i, gray)
            }
        }
    }

    private fun checkContainLetter(letter: CharSequence): Boolean {
        return viewModel.questionWord.contains(letter)
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

        val parsedMeaning = viewModel.questionMeaning

        DialogStatistics.Builder(
            context = this,
            sp = sp,
            positiveButtonClickListener = ::setOnPositiveButtonClickListener,
            word = viewModel.questionWord.joinToString(),
            meaning = parsedMeaning
        ).build()
            .show()
    }

    private fun setOnPositiveButtonClickListener() {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()

    }

    private fun setSuccessPreference(successAtRow: Int) {
        val totalTry = sp.getInt("totalTry", 0)
        val key = "successAt$successAtRow"
        val successAtRowNum = sp.getInt(key, 0)
        val consecutiveSuccessNum = sp.getInt("consecutiveSuccess", 0)

        with(sp.edit()) {
            //총시도
            this.putInt("totalTry", totalTry + 1)
            //현재 연속 성공
            this.putInt("consecutiveSuccess", consecutiveSuccessNum + 1)
            //몇번째에?
            this.putInt("successAt$successAtRow", successAtRowNum + 1)
            apply()
        }
    }

    private fun setFailPreference() {
        val totalTry = sp.getInt("totalTry", 0)
        val consecutiveSuccessNum = sp.getInt("consecutiveSuccess", 0)
        val consecutiveSuccessArray = sp.getStringSet("consecutiveSuccessArray", setOf<String>())

        with(sp.edit()) {
            this.putInt("totalTry", totalTry + 1)
            this.putInt("consecutiveSuccess", 0) //현재 연속 성공 Reset

            consecutiveSuccessArray.isNullOrEmpty().not().let {
                val set = if (it) {
                    consecutiveSuccessArray!!.add(consecutiveSuccessNum.toString())
                    consecutiveSuccessArray.toSet()
                } else setOf(consecutiveSuccessNum.toString())
                this.putStringSet("consecutiveSuccessArray", set)
            }
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.let {
            if (it.isShowing) it.dismiss()
        }
    }
}