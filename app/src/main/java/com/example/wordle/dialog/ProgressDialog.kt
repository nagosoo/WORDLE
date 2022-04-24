package com.example.wordle.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.bumptech.glide.Glide
import com.example.wordle.R
import com.example.wordle.databinding.DialogProgressBinding

class ProgressDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogProgressBinding.inflate(layoutInflater)
        Glide.with(context).load(R.drawable.go_gif).into(binding.imageView)
        setContentView(binding.root)
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}