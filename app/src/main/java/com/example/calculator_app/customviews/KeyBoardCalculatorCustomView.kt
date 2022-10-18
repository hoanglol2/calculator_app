package com.example.calculator_app.customviews

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import com.example.calculator_app.databinding.LayoutCalButtonsBinding

class KeyBoardCalculatorCustomView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attributeSet, defStyleAttr), View.OnClickListener {

    private var inpConnection: InputConnection? = null
    private val keyValues: HashMap<String, String> = hashMapOf()
    private var binding: LayoutCalButtonsBinding
    private var inputOperandFirst: Int = 0
    private var inputOperandSecond: Int = 0
    private var operator: String = ""
    private var onClickDoneListener: (() -> Unit)? = null

    init {
        binding = LayoutCalButtonsBinding.inflate(
            LayoutInflater.from(context), this, true
        )
        initListener()
    }

    fun setInpConnection(inp: InputConnection?) {
        inpConnection = inp
    }

    fun setOnClickDone(listener: () -> Unit) {
        onClickDoneListener = listener
    }

    private fun initListener() {
        binding.apply {
            root.children.forEach {
                keyValues[it.id.toString()] = (it as? AppCompatTextView)?.text?.toString() ?: ""
                it.setOnClickListener(this@KeyBoardCalculatorCustomView)
            }
        }
    }
    
    private fun getCurrentInput() = inpConnection?.getExtractedText(
        ExtractedTextRequest(), 0)?.text?.toString() ?: ""

    private fun resetInput() {
        inpConnection?.apply {
            val beforeCursorText = getTextBeforeCursor(getCurrentInput().length, 0)
            val afterCursorText = getTextAfterCursor(getCurrentInput().length, 0)
            if (beforeCursorText != null && afterCursorText != null) {
                deleteSurroundingText(
                    beforeCursorText.length,
                    afterCursorText.length
                )
            } else {
                commitText("", 1)
            }
        }
    }

    private fun resetValue() {
        inputOperandFirst = 0
        inputOperandSecond = 0
        operator = ""
    }
    
    private fun setupLatestValue() {
        val value = getCurrentInput().toIntOrNull() ?: 0
        if (getCountOfBtnFuncSelected() == 0) {
            inputOperandFirst = value
        } else {
            inputOperandSecond = value
        }
    }

    private fun clearBtnFunctionSelected() {
        binding.apply {
            getButtonFuncList().forEach { it.isSelected = false }
        }
    }

    private fun getButtonFuncList(): List<AppCompatTextView> {
        binding.apply {
            return listOf(btnDivide, btnMulti, btnMinus, btnPlus)
        }
    }
    
    private fun getCountOfBtnFuncSelected() = getButtonFuncList().count { it.isSelected }

    private fun delInput() {
        inpConnection?.apply {
            val selectedText = getSelectedText(0)
            if (TextUtils.isEmpty(selectedText)) {
                deleteSurroundingText(1, 0)
            } else {
                commitText("", 1)
            }
            setupLatestValue()
        }
    }

    private fun calculateInput() {
        if (inputOperandFirst != 0 && inputOperandSecond != 0 && operator.isNotEmpty()) {
            resetInput()
            val value = (when (operator) {
                "÷" -> inputOperandFirst / inputOperandSecond
                "x" -> inputOperandFirst * inputOperandSecond
                "-" -> inputOperandFirst - inputOperandSecond
                "+" -> inputOperandFirst + inputOperandSecond
                else -> 0
            }).toString()
            inpConnection?.commitText(value, 1)
        }
        clearBtnFunctionSelected()
    }

    override fun onClick(v: View?) {
        if (inpConnection == null) return
        binding.apply {
            val value = keyValues[v?.id.toString()]
            when (v) {
                btnReset -> {
                    resetInput()
                    resetValue()
                    clearBtnFunctionSelected()
                }
                btnDel -> {
                    delInput()
                }
                btnOk -> {
                    calculateInput()
                    if (btnOk.text == "OK") {
                        onClickDoneListener?.invoke()
                    }
                }
                btnDivide, btnMulti, btnMinus, btnPlus -> {
                    btnDivide.isSelected = v == btnDivide
                    btnMulti.isSelected = v == btnMulti
                    btnMinus.isSelected = v == btnMinus
                    btnPlus.isSelected = v == btnPlus

                    inputOperandFirst = getCurrentInput().toIntOrNull() ?: 0
                    operator = (v as? AppCompatTextView)?.text?.toString() ?: ""
                }
                else -> {
                    if (getCountOfBtnFuncSelected() > 0 && inputOperandSecond == 0) {
                        resetInput()
                        inputOperandSecond = (v as? AppCompatTextView)?.text?.toString()?.toIntOrNull() ?: 0
                    }
                    inpConnection?.commitText(value, 1)
                }
            }
            btnOk.text = if (getCountOfBtnFuncSelected() > 0) "=" else "OK"
        }
    }
}