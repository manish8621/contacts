package com.mk.contacts.model.repository

import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mk.contacts.model.FAVOURITE
import com.mk.contacts.model.GROUP
import com.mk.contacts.model.NO_FILTER
import com.mk.contacts.model.room.*
import com.mk.contacts.model.room.relations.ContactGroupsCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactRepository(private val database: ContactDatabase) {

    suspend fun addGroup(groupName:String){
        withContext(Dispatchers.IO){
            database.contactDao.insertGroup(Group(groupName))
        }
    }

    suspend fun isContactExists(name:String)= withContext(Dispatchers.IO){
        database.contactDao.contactExists(name)
    }



    suspend fun addContact(contact: Contact) = withContext(Dispatchers.IO){
            database.contactDao.insertContact(contact)
        }
    suspend fun addContactToGroup(id:Long,groupName: String){
        withContext(Dispatchers.IO){
            database.contactDao.insertContactGroupRef(ContactGroupsCrossRef(id, groupName))
        }
    }
    suspend fun updateContact(contact: Contact){
        database.contactDao.updateContact(contact)
    }


    fun getContactsWithGroups() = database.contactDao.getContactsWithGroups()

    fun getContacts() = database.contactDao.getContacts()

    fun getGroups() = database.contactDao.getGroups()

    fun getContactWithGroupsLive(id:Long) = database.contactDao.getContactWithGroupsLive(id)

    fun getContactWithGroups(id:Long) = database.contactDao.getContactWithGroups(id)

    fun getPersonalDetail() = database.contactDao.getPersonalDetail()

    suspend fun getContactsByGroup(groupName: String)= withContext(Dispatchers.IO){
        database.contactDao.getContactsByGroup(groupName)
    }

    fun getContactsByFilter(filterType:Int,groupName: String): LiveData<List<Contact>> {
        return when(filterType){
            NO_FILTER -> database.contactDao.getContacts()
            FAVOURITE -> database.contactDao.getFavContacts()
            GROUP -> Transformations.map(database.contactDao.getContactsByGroup(groupName)){it.contacts}
            else -> throw IllegalArgumentException("illegal argument in repository")
        }
    }

    //delete
    /*
    *use this function to delete a contact completely
    *  **/
    suspend fun deleteContactFromDb(contact: Contact){
        withContext(Dispatchers.IO){//delete the ref
            database.contactDao.deleteContactGroupRef(contact.id)
            //then delete contact
            database.contactDao.deleteContact(contact)
        }
    }
    suspend fun deleteContactGroupRefs(id:Long){
        withContext(Dispatchers.IO){ database.contactDao.deleteContactGroupRef(id) }
    }


}