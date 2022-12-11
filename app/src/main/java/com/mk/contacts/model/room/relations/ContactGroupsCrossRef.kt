package com.mk.contacts.model.room.relations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_groups_cross_ref", primaryKeys = ["id","group_name"])
data class ContactGroupsCrossRef(
    val id:Long,
    @ColumnInfo(name = "group_name")
    val groupName: String
)