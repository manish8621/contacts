package com.mk.contacts.model.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mk.contacts.model.room.relations.ContactGroupsCrossRef

data class GroupWithContacts(
    @Embedded
    val group: Group,
    @Relation(
        parentColumn = "group_name",
        entityColumn = "id",
        associateBy = Junction(ContactGroupsCrossRef::class)
    )
    val contacts : List<Contact>
)