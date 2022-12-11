package com.mk.contacts.model.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mk.contacts.model.room.relations.ContactGroupsCrossRef


data class ContactWithGroups(
    @Embedded
    val contact: Contact,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_name",
        associateBy = Junction(ContactGroupsCrossRef::class)
    )
    val groups : List<Group>
)