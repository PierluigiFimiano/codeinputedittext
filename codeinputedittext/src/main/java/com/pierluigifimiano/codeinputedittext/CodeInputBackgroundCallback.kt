package com.pierluigifimiano.codeinputedittext

interface CodeInputBackgroundCallback {

    fun onMeasureChanged(width: Int, height: Int, paddingStart: Int, paddingTop: Int,
                         paddingEnd: Int, paddingBottom: Int)

    fun onCodeLengthChanged(length: Int)

}