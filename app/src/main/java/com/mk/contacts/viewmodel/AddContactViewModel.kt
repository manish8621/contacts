package com.mk.contacts.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mk.contacts.model.repository.ContactRepository
import com.mk.contacts.model.room.Contact
import com.mk.contacts.model.room.ContactWithGroups
import com.mk.contacts.model.room.Group
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class AddContactViewModel(private val repository: ContactRepository):ViewModel() {
    val contactWithGroups = MutableLiveData<ContactWithGroups>(ContactWithGroups(Contact(name = "", photo = "", mobileNumber1 = "", mobileNumber2 = "", mobileNumber3 = "", mail = "", isFavourite = false),
        listOf<Group>()
    ))

    val contact = MutableLiveData<Contact>(Contact())

    val statusText = MutableLiveData("")

    val groups = repository.getGroups()

    private val groupsSelected = arrayListOf<Group>()

    fun clearStatusText(){
        statusText.value = ""
    }
    fun addContact(name: String){
        CoroutineScope(Dispatchers.IO).launch{
            if(repository.isContactExists(name)) {
                statusText.postValue("Contact with this name already exists")
                return@launch
            }
            contact.value?.let {
                val id = repository.addContact(it)

                groupsSelected.forEach { group->
                    repository.addContactToGroup(id,group.groupName)
                }

                statusText.postValue("contact saved")
            }
        }
    }
    fun getContactWithGroups(id:Long){
        viewModelScope.launch(Dispatchers.IO) {
            val contactWithGroups = repository.getContactWithGroups(id)
            contact.postValue(contactWithGroups.contact)
//            contactWithGroups.groups
        }
    }

    fun addGroup(group: Group){
        if(!groupsSelected.contains(group))
            groupsSelected.add(group)
    }
    fun removeGroup(group: Group){
        if(groupsSelected.contains(group))
            groupsSelected.remove(group)
    }

    fun updateContact(name: String) {
        CoroutineScope(Dispatchers.IO).launch{
            contact.value?.let {
                repository.updateContact(it)

                //remove contact groups refs
                repository.deleteContactGroupRefs(it.id)

                groupsSelected.forEach { group->
                    repository.addContactToGroup(it.id,group.groupName)
                }

                statusText.postValue("contact updated")
            }
        }
    }
}
class AddContactViewModelFactory(private val repository: ContactRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddContactViewModel::class.java))
            return AddContactViewModel(repository) as T
        throw IllegalArgumentException("illegal arg at add contact factory")
    }
}