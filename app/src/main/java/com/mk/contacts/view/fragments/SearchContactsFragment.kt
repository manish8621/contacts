package com.mk.contacts.view.fragments

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mk.contacts.ContactsApplication
import com.mk.contacts.R
import com.mk.contacts.databinding.FragmentSearchContactsBinding
import com.mk.contacts.databinding.FragmentShowContactBinding
import com.mk.contacts.databinding.FragmentShowContactsBinding
import com.mk.contacts.model.NO_FILTER
import com.mk.contacts.model.contactProvider.ContactResolverUtil
import com.mk.contacts.view.adapter.ContactsListAdapter
import com.mk.contacts.viewmodel.ShowContactsViewModel
import com.mk.contacts.viewmodel.ShowContactsViewModelFactory


class SearchContactsFragment : Fragment() {
    private lateinit var binding : FragmentSearchContactsBinding
    private lateinit var adapter: ContactsListAdapter
    private lateinit var cursor: Cursor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchContactsBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        //search view
        binding.contactSearchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if(newText.isNotEmpty()){

                        if(::adapter.isInitialized.not()) {
                            refreshCursor(it)?.let { cursor->
                                adapter = ContactsListAdapter(cursor)
                                binding.contactsRv.adapter = adapter
                                setOnClickListeners()
                            }
                        }
                        else refreshCursor(it)

                        showRv()
                    }
                    else hideRv()
                }

                return true
            }

        })

    }

    private fun setOnClickListeners() {
        //set onclick listener for each item in recycler view
        if(::adapter.isInitialized)
            adapter.setOnClickListener {
                findNavController().navigate(SearchContactsFragmentDirections.actionSearchContactsFragment2ToShowContactFragment(it))
            }
    }

    fun showRv(){
        binding.contactsRv.visibility = View.VISIBLE
    }
    fun hideRv(){
        binding.contactsRv.visibility = View.GONE
    }



    //get a new cursor

    private fun refreshCursor(searchQuery:String):Cursor? {
        closeCursor()
        return ContactResolverUtil.getSearchCursor(searchQuery,requireActivity().contentResolver)?.let {
            cursor = it
            if(::adapter.isInitialized) adapter.submitCursor(it)
            cursor
        }
    }

    private fun closeCursor() {
        if (::cursor.isInitialized)
            cursor.close()
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onDestroy() {
        super.onDestroy()
        closeCursor()
    }
}