package com.mk.contacts

import android.app.Application
import com.mk.contacts.model.repository.ContactRepository
import com.mk.contacts.model.room.ContactDatabase
import com.mk.contacts.model.room.contactDataBase

class ContactsApplication:Application() {
    private val database :ContactDatabase by lazy { contactDataBase.getDatabase(applicationContext) }
    val repository : ContactRepository by lazy { ContactRepository(database) }
}