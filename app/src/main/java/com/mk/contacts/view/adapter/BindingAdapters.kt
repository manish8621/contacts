package com.mk.contacts.view.adapter

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.android.material.textfield.TextInputEditText
import com.mk.contacts.R
import com.mk.contacts.model.room.Contact
import com.mk.contacts.view.filePathToUri
import com.mk.contacts.view.toFormattedString
import java.util.*


@BindingAdapter("date")
fun formatDate(textInput: TextInputEditText,date: Date?){
    date?.let {
        textInput.setText(date.toFormattedString())
    }
}

@BindingAdapter("imagePath")
fun loadImageFromUri(imageView: ImageView,path:String?){
    path?.let {
        if(path.isNotEmpty() && path.isNotBlank())
            imageView.setImageURI(filePathToUri(it))
    }
}

@BindingAdapter("imageUri")
fun imageUri(imageView: ImageView,contact: Contact?){
    contact?.let {
        if(it.photo.isNotEmpty()) {
            imageView.setImageURI(Uri.parse(it.photo))
        }
        else
        {
            //load text drawable

            val color:Int = ColorGenerator.MATERIAL.randomColor
            val textDrawable = TextDrawable.Builder()
                .setWidth(100)
                .setBold()
                .setHeight(100)
                .setColor(color)
                .setTextColor(imageView.context.getColor(R.color.black))
                .setText("${ it.name[0] }")
                .build()
            imageView.setImageDrawable(null)
            imageView.setImageDrawable(textDrawable)
        }

    }
}
