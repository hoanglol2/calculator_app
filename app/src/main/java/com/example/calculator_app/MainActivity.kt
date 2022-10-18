package com.example.calculator_app

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.calculator_app.databinding.ActivityMainBinding
import com.example.calculator_app.extensions.hideKeyboard
import com.example.calculator_app.extensions.showKeyboard
import com.example.calculator_app.extensions.slideUp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isCalMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initListener()
    }

    private fun initViews() {
        binding.apply {
            editText.showSoftInputOnFocus = !isCalMode
            val ic: InputConnection? = editText.onCreateInputConnection(EditorInfo())
            keyboardCalculator.setInpConnection(ic)
        }
    }

    private fun initListener() {
        binding.apply {
            ivSwitchCalculator.setOnClickListener {
                isCalMode = !isCalMode
                if (isCalMode) {
                    it.hideKeyboard()
                } else {
                    it.showKeyboard()
                }
                keyboardCalculator.isVisible = editText.isFocused && isCalMode
            }
            editText.setOnFocusChangeListener { _, isFocus ->
                keyboardCalculator.isVisible = isFocus && isCalMode
                rlKeyboardHeader.isVisible = isFocus
                if (isFocus) {
                    keyboardCalculator.slideUp()
                }
            }
            keyboardCalculator.setOnClickDone {
                closeKeyboardCalculator()
            }
            tvDone.setOnClickListener {
                closeKeyboardCalculator()
            }
        }
    }

    private fun closeKeyboardCalculator() {
        binding.apply {
            rlKeyboardHeader.isVisible = false
            keyboardCalculator.isVisible = false
            editText.hideKeyboard()
            if (editText.text.isNullOrEmpty()) {
                editText.setText("0")
            }
            isCalMode = true
            editText.clearFocus()
        }
    }
}