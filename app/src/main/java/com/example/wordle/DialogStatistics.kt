package com.example.wordle

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.wordle.databinding.DialogStatisticsBinding
import splitties.dimensions.dip

class DialogStatistics(context: Context, val sp: SharedPreferences) : AlertDialog(context) {
    private val inflater = LayoutInflater.from(context)
    private val binding = DialogStatisticsBinding.inflate(inflater)
    var matchParentWidth = 0

    init {
        setView(binding.root)

        binding.viewOne.post {
            matchParentWidth = binding.viewOne.width //height is ready
            setData()
        }
    }

    private fun setData() {
        val totalTry = sp.getInt("totalTry", 0)

        binding.tvTotalTry.text = totalTry.toString()
        binding.tvPercentage.text = "${(getSuccessNumSum() / totalTry * 100)}%"

        binding.tvOne.text = getSuccessNumAt(1).toString()
        binding.tvTwo.text = getSuccessNumAt(2).toString()
        binding.tvThree.text = getSuccessNumAt(3).toString()
        binding.tvFour.text = getSuccessNumAt(4).toString()
        binding.tvFive.text = getSuccessNumAt(5).toString()
        binding.tvSix.text = getSuccessNumAt(6).toString()

        val maxMap = getMaxSuccess()
        val maxValue = maxMap.values.map { it }[0]
        binding.viewOne.layoutParams.width =
            context.dip(matchParentWidth * getSuccessNumAt(1) / maxValue)
        binding.viewTwo.layoutParams.width =
            context.dip(matchParentWidth * getSuccessNumAt(2) / maxValue)
        binding.viewThree.layoutParams.width =
            context.dip(matchParentWidth * getSuccessNumAt(3) / maxValue)
        binding.viewFour.layoutParams.width =
            context.dip(matchParentWidth * getSuccessNumAt(4) / maxValue)
        binding.viewFive.layoutParams.width =
            context.dip(matchParentWidth * getSuccessNumAt(5) / maxValue)
        binding.viewSix.layoutParams.width =
            context.dip(matchParentWidth * getSuccessNumAt(6) / maxValue)
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

    class Builder(private val context: Context, private val sp: SharedPreferences) {
        fun build(): DialogStatistics {
            return DialogStatistics(context, sp)
        }
    }

}