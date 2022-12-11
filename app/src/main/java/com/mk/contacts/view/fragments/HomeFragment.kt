package com.mk.contacts.view.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mk.contacts.ContactsApplication
import com.mk.contacts.R
import com.mk.contacts.databinding.FragmentHomeBinding
import com.mk.contacts.databinding.FragmentShowContactsBinding
import com.mk.contacts.databinding.GroupListItemLayoutBinding
import com.mk.contacts.databinding.MultipleOptionItemLayoutBinding
import com.mk.contacts.model.FAVOURITE
import com.mk.contacts.model.GROUP
import com.mk.contacts.model.contactProvider.ContactResolverUtil
import com.mk.contacts.model.room.Group
import com.mk.contacts.view.adapter.ContactsListAdapter
import com.mk.contacts.view.adapter.GROUP_ITEM_VIEW_TYPE
import com.mk.contacts.view.adapter.GroupAdapter
import com.mk.contacts.view.toFormattedString
import com.mk.contacts.viewmodel.HomeViewModel
import com.mk.contacts.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
//    private val adapter = GroupAdapter(viewType = GROUP_ITEM_VIEW_TYPE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        //vmodel
        val repository = (requireActivity().application as ContactsApplication).repository
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[HomeViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setOnClickListeners()
        setupUi()
    }

    private fun setupUi() {
        val contact = ContactResolverUtil.getPersonalDetails(requireActivity().contentResolver)
        contact?.let {
            viewModel.personalContact.value = contact
        }
    }

    private fun setOnClickListeners() {

        binding.favCard.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToShowContactsFragment(filterType = FAVOURITE))
        }


        binding.addContactBtn.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddContactFragment(isPersonalDetail = false, editMode = false))
        }
        binding.personalDetailLayout.addDetailsBtn.setOnClickListener{
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddContactFragment(isPersonalDetail = true, editMode = false))
            Toast.makeText(context, "add profile details:Under devel", Toast.LENGTH_SHORT).show()

        }
//
//        binding.personalDetailLayout.root.setOnClickListener{
//            viewModel.personalContact.value?.let {
//                if(it.id!=-1L)
//                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToShowContactFragment(contactId = it.id))
//            }
//        }

    }

    private fun setObservers() {

        viewModel.personalContact.observe(viewLifecycleOwner){
            it?.let {
                //remove overlay
                binding.personalDetailLayout.addActionGroup.visibility = View.GONE
                animateCardX(binding.personalDetailLayout.root)

                //update ui
                with(binding.personalDetailLayout){
                    nameTv.text = it.name
                    if(it.photo.isNotEmpty()) photoIv.setImageURI(Uri.parse(it.photo))
                }
            }
            if(it == null){
                //if contact is null
                binding.personalDetailLayout.addActionGroup.visibility = View.VISIBLE
                animateCardY(binding.personalDetailLayout.root)
            }
        }
    }
    fun animateCardX(root:View){
       root.animate().setDuration(250L).rotationXBy(2.3F).withEndAction {
            root.animate().setDuration(250L).rotationXBy(-2.3F).withEndAction {
                root.animate().setDuration(200L).rotationXBy(2.3F).withEndAction {
                    root.animate().setDuration(200L).rotationXBy(-2.3F).start()
                }.start()
            }.start()
        }.start()
    }
    fun animateCardY(root:View){

        root.animate().setDuration(250L).rotationYBy(2.3F).withEndAction {
            root.animate().setDuration(250L).rotationYBy(-2.3F).withEndAction {
                root.animate().setDuration(200L).rotationYBy(2.3F).withEndAction {
                    root.animate().setDuration(200L).rotationYBy(-2.3F).start()
                }.start()
            }
                .start()
        }.start()
    }

    private fun setTextIfNotEmpty(text: String, textView: TextView, visibleView: View) {
        if (text.isNotEmpty()) {
            visibleView.visibility = View.VISIBLE
            textView.text = text
        }
    }

}
