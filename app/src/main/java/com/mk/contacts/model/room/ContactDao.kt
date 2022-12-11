package com.mk.contacts.model.room

import android.provider.ContactsContract.Groups
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mk.contacts.model.room.relations.ContactGroupsCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext




@Dao
interface ContactDao{
    @Query("SELECT COUNT(*)!=0 FROM contacts_table where name=:name")
    fun contactExists(name: String):Boolean


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGroup(group: Group)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: Contact): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertContactGroupRef(contactGroupsCrossRef: ContactGroupsCrossRef)


    @Update
    fun updateContact(contact: Contact)


    @Transaction
    @Query("SELECT * FROM contacts_table ORDER BY name")
    fun getContactsWithGroups():LiveData<List<ContactWithGroups>>

    @Query("SELECT * FROM contacts_table ORDER BY name")
    fun getContacts():LiveData<List<Contact>>

    @Query("SELECT * FROM contacts_table WHERE is_favourite=1 ORDER BY name")
    fun getFavContacts():LiveData<List<Contact>>

    @Transaction
    @Query("SELECT * FROM contacts_table where id = :id")
    fun getContactWithGroupsLive(id:Long):LiveData<ContactWithGroups>

    @Transaction
    @Query("SELECT * FROM contacts_table where id = :id")
    fun getContactWithGroups(id:Long):ContactWithGroups

    @Query("SELECT * FROM groups_table")
    fun getGroups():LiveData<List<Group>>

    @Query("SELECT * FROM contacts_table where id = -1")
    fun getPersonalDetail():LiveData<Contact>

    @Transaction
    @Query("SELECT * FROM groups_table where group_name=:groupName")
    fun getContactsByGroup(groupName: String):LiveData<GroupWithContacts>


    @Query("Delete from contact_groups_cross_ref where id = :id")
    fun deleteContactGroupRef(id:Long)
    @Delete
    fun deleteContact(contact: Contact)

}