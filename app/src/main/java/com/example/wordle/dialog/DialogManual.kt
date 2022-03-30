package com.example.wordle.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.wordle.databinding.DialogManualBinding

class DialogManual(context: Context) : AlertDialog(context) {
    init {
        val inflater = LayoutInflater.from(context)
        val binding = DialogManualBinding.inflate(inflater)

        setView(binding.root)
    }

    class Builder(private val context: Context) {
        fun build(): DialogManual {
            return DialogManual(context)
        }
    }
}