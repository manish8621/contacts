package com.mk.contacts.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mk.contacts.model.repository.ContactRepository
import com.mk.contacts.model.room.Contact

class HomeViewModel(private val repository: ContactRepository): ViewModel() {
    val groups = repository.getGroups()
    val personalContact = MutableLiveData<Contact>()
}

class HomeViewModelFactory(private val repository: ContactRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(repository) as T
        throw IllegalArgumentException("illegal arg at home factory")
    }
}