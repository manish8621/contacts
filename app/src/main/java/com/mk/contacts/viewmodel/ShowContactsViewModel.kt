package com.mk.contacts.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mk.contacts.model.repository.ContactRepository
import com.mk.contacts.model.room.Group

class ShowContactsViewModel(private val repository: ContactRepository,filterType:Int,group:String):ViewModel() {
    val contacts =  repository.getContactsByFilter(filterType,group)
}

class ShowContactsViewModelFactory(private val repository: ContactRepository,
                                   private val  filterType:Int, val group:String): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShowContactsViewModel::class.java))
            return ShowContactsViewModel(repository,filterType,group) as T
        throw IllegalArgumentException("illegal arg at show contact factory")
    }
}