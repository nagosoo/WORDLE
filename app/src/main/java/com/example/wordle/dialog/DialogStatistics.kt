package com.example.wordle.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.wordle.databinding.DialogStatisticsBinding
import kotlin.math.roundToInt

class DialogStatistics(
    context: Context,
    private val sp: SharedPreferences,
    private val positiveButtonClickListener: (() -> (Unit))? = null,
    word: String? = null,
    meaning: String? = null
) : AlertDialog(context) {
    private val inflater = LayoutInflater.from(context)
    private val binding = DialogStatisticsBinding.inflate(inflater)
    private var matchParentWidth = 0

    init {
        setView(binding.root)

        binding.viewOne.post {
            matchParentWidth = binding.viewOne.width //height is ready
            setData()
        }
        if (word.isNullOrEmpty().not()) binding.tvAnswer.text = "정답은 '$word' 이었습니다."
        if (meaning.isNullOrEmpty().not()) binding.tvMeaning.text = meaning
        if (positiveButtonClickListener == null) {
            binding.tvAnswer.isVisible = false
            binding.tvMeaning.isVisible = false
            binding.buttonPositive.isVisible = false
            binding.buttonNegative.isVisible = false
        }
        setOnClickListener()
    }

    private fun setData() {
        val totalTry = sp.getInt("totalTry", 0)
        val successiveSuccess = sp.getInt("successiveSuccess", 0)

        binding.tvTotalTry.text = totalTry.toString()
        val decimal = if (totalTry != 0) getSuccessNumSum() / totalTry.toDouble() else 0.toDouble()
        val quotient = (decimal * 100).roundToInt()
        binding.tvPercentage.text = "$quotient%"
        binding.tvCurrentSuccessive.text = successiveSuccess.toString()
        val set = sp.getStringSet("successiveSuccessArray", setOf())
        set.isNullOrEmpty().not().let {
            val max = if (it) {
                val maxSS = set!!.map { it.toInt() }.maxOf { it }
                maxOf(maxSS, successiveSuccess)
            } else successiveSuccess

            binding.tvMaxSuccessive.text = max.toString()
        }

        binding.tvOne.text = getSuccessNumAt(1).toString()
        binding.tvTwo.text = getSuccessNumAt(2).toString()
        binding.tvThree.text = getSuccessNumAt(3).toString()
        binding.tvFour.text = getSuccessNumAt(4).toString()
        binding.tvFive.text = getSuccessNumAt(5).toString()
        binding.tvSix.text = getSuccessNumAt(6).toString()

        val maxMap = getMaxSuccess()
        val maxValue = maxMap.values.map { it }[0]

        if (maxValue == 0) {
            binding.viewOne.layoutParams.width = 0
            binding.viewTwo.layoutParams.width = 0
            binding.viewThree.layoutParams.width = 0
            binding.viewFour.layoutParams.width = 0
            binding.viewFive.layoutParams.width = 0
            binding.viewSix.layoutParams.width = 0
        } else {
            binding.viewOne.layoutParams.width = matchParentWidth * getSuccessNumAt(1) / maxValue
            binding.viewTwo.layoutParams.width = matchParentWidth * getSuccessNumAt(2) / maxValue
            binding.viewThree.layoutParams.width = matchParentWidth * getSuccessNumAt(3) / maxValue
            binding.viewFour.layoutParams.width = matchParentWidth * getSuccessNumAt(4) / maxValue
            binding.viewFive.layoutParams.width = matchParentWidth * getSuccessNumAt(5) / maxValue
            binding.viewSix.layoutParams.width = matchParentWidth * getSuccessNumAt(6) / maxValue
        }

    }

    private fun setOnClickListener() {
        binding.buttonPositive.setOnClickListener {
            dismiss()
            positiveButtonClickListener?.let { it -> it() }
        }
        binding.buttonNegative.setOnClickListener {
            dismiss()
        }
        binding.ButtonShare.setOnClickListener {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("appUrl", "https://worlde.page.link/Tbeh")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "링크가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSuccessNumSum(): Int {
        var totalSuccessNum = 0
        for (i in 1..6) {
            totalSuccessNum += sp.getInt("successAt$i", 0)
        }
        return totalSuccessNum
    }

    private fun getSuccessNumAt(trial: Int): Int {
        return sp.getInt("successAt$trial", 0)
    }

    private fun getMaxSuccess(): Map<Int, Int> {
        val list = mutableListOf<Int>()
        for (i in 1..6) {
            list.add(sp.getInt("successAt$i", 0))
        }
        val max = list.maxOf { it }
        return mapOf(list.indexOf(max) + 1 to max)
    }


    class Builder(
        private val context: Context,
        private val sp: SharedPreferences,
        private val positiveButtonClickListener: (() -> (Unit))? = null,
        private var word: String? = null,
        private var meaning: String? = null
    ) {
        fun build(): DialogStatistics {
            return DialogStatistics(context, sp, positiveButtonClickListener, word, meaning)
        }
    }

}