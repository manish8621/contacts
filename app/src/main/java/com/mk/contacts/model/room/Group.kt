package com.mk.contacts.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "groups_table")
data class Group(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="group_name")
    val groupName:String
)