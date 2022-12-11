package com.mk.contacts.view

import android.net.Uri
import android.widget.DatePicker
import java.io.File
import java.util.*


fun DatePicker.getDate(): Date {
    return Calendar.getInstance().also {
        it.set(year,month,dayOfMonth)
    }.time
}
fun Date.toFormattedString() = (android.text.format.DateFormat.format("dd/MM/yyyy", this)).toString()
fun filePathToUri(path:String) = Uri.fromFile(File(path))