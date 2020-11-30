package com.pierluigifimiano.codeinputedittextexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pierluigifimiano.codeinputedittext.CodeInputEditText
import com.pierluigifimiano.codeinputedittext.DefaultCodeInputDrawable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupCodeInputEditText()
    }

    private fun setupCodeInputEditText() {
        val codeInput: CodeInputEditText = findViewById(R.id.code_input_edit_text) ?: return
        codeInput.background = DefaultCodeInputDrawable()
    }

}