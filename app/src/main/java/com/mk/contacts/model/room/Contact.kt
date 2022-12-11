package com.mk.contacts.model.room


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "contacts_table")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    var id:Long=0,
    var name:String="",
    var photo:String="",
    @ColumnInfo(name = "mobile_number1")
    var mobileNumber1 :String="",
    @ColumnInfo(name = "mobile_number2")
    var mobileNumber2 :String="",
    @ColumnInfo(name = "mobile_number3")
    var mobileNumber3 :String="",
    var mail:String="",
    var dob :Date?=null,
    @ColumnInfo(name="is_favourite")
    var isFavourite:Boolean=false
)
