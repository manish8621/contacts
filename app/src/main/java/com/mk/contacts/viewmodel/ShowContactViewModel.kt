package com.mk.contacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mk.contacts.model.repository.ContactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowContactViewModel(private val repository: ContactRepository): ViewModel() {

//    val contactWithGroups = repository.getContactWithGroupsLive(contactId)
    fun updateFavContact() {
//        viewModelScope.launch(Dispatchers.IO) {
//            contactWithGroups.value?.contact?.let{
//                repository.updateContact(
//                    contact = it.copy(isFavourite = it.isFavourite.not())
//                )
//            }
//        }
    }

    fun deleteContact(contactId: Long) {
//        CoroutineScope(Dispatchers.IO).launch {
//            contactWithGroups.value?.contact?.let {
//                repository.deleteContactFromDb(it)
//            }
//
//        }
    }

}

class ShowContactViewModelFactory(private val repository: ContactRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShowContactViewModel::class.java))
            return ShowContactViewModel(repository) as T
        throw IllegalArgumentException("illegal arg at show a contact factory")
    }
}