package com.mk.contacts.view.fragments

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mk.contacts.ContactsApplication
import com.mk.contacts.R
import com.mk.contacts.databinding.FragmentShowContactsBinding
import com.mk.contacts.model.FAVOURITE
import com.mk.contacts.model.NO_FILTER
import com.mk.contacts.model.contactProvider.ContactResolverUtil
import com.mk.contacts.view.adapter.ContactsListAdapter
import com.mk.contacts.viewmodel.ShowContactsViewModel
import com.mk.contacts.viewmodel.ShowContactsViewModelFactory
import kotlinx.coroutines.internal.artificialFrame

class ShowContactsFragment : Fragment() {

    private lateinit var binding : FragmentShowContactsBinding
    private lateinit var viewModel: ShowContactsViewModel
    private val args :ShowContactsFragmentArgs by navArgs()
    private lateinit var adapter: ContactsListAdapter

    private lateinit var cursor: Cursor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowContactsBinding.inflate(inflater,container,false)

        //vmodel
        val repository = (requireActivity().application as ContactsApplication).repository
        //gets nav args ,if it is null continue with default value

        val factory = ShowContactsViewModelFactory(repository,args.filterType,args.groups)
        viewModel = ViewModelProvider(this,factory)[ShowContactsViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupui()
        setOnClickListeners()
        setPersonalContactDetails()
    }

    private fun setPersonalContactDetails() {
        ContactResolverUtil.getContactsCursor(requireActivity().contentResolver,)
    }

    private fun setupui() {

        //setup recycler view
        refreshCursor()
        if(::cursor.isInitialized) {
            adapter = ContactsListAdapter(cursor)
            binding.contactsRv.adapter = adapter
        }
    }



    private fun setOnClickListeners() {

        if(args.filterType == NO_FILTER) binding.addBtn.visibility =View.VISIBLE
        binding.addBtn.setOnClickListener{
            findNavController().navigate(R.id.action_showContactsFragment_to_addContactFragment)
        }

        //set onclick listener for each item in recycler view
        if(::adapter.isInitialized)
            adapter.setOnClickListener {
                Toast.makeText(context, "id:$it", Toast.LENGTH_SHORT).show()
                findNavController().navigate(ShowContactsFragmentDirections.actionShowContactsFragmentToShowContactFragment(it.toLong()))
            }
    }


    fun showStatus(text:String){
        binding.statusTv.visibility = View.VISIBLE
        binding.statusTv.text = text
    }
    fun hideStatus(){
        binding.statusTv.visibility = View.GONE
    }

    //
    //get a new cursor
    private fun refreshCursor() {
        val selection = if(args.filterType == FAVOURITE) "${Phone.STARRED} = 1"
                        else null
        closeCursor()
        ContactResolverUtil.getContactsCursor(requireActivity().contentResolver, selection= selection)?.let {
            cursor = it
            if(::adapter.isInitialized) adapter.submitCursor(it)
        }
    }

    private fun closeCursor() {
        if (::cursor.isInitialized)
            cursor.close()
    }

    override fun onResume() {
        super.onResume()
        refreshCursor()
    }
    override fun onDestroy() {
        super.onDestroy()
        closeCursor()
    }

}