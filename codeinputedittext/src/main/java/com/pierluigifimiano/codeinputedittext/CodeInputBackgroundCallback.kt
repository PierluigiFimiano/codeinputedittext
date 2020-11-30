package com.pierluigifimiano.codeinputedittext

interface CodeInputBackgroundCallback {

    fun onMeasureChanged(start: Int, width: Int)

    fun onCodeLengthChanged(length: Int)

}