package com.mk.contacts.view.fragments

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mk.contacts.ContactsApplication
import com.mk.contacts.R
import com.mk.contacts.databinding.FragmentShowContactBinding
import com.mk.contacts.model.PERSONAL_DETAIL_ID
import com.mk.contacts.model.contactProvider.ContactResolverUtil
import com.mk.contacts.view.adapter.GROUP_OPTION_VIEW_TYPE
import com.mk.contacts.view.adapter.GroupAdapter
import com.mk.contacts.view.toFormattedString
import com.mk.contacts.viewmodel.ShowContactViewModel
import com.mk.contacts.viewmodel.ShowContactViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

class ShowContactFragment : Fragment() {

    private lateinit var binding : FragmentShowContactBinding
    private lateinit var viewModel: ShowContactViewModel
    private val adapter = GroupAdapter(viewType = GROUP_OPTION_VIEW_TYPE)
    private val args :ShowContactFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowContactBinding.inflate(inflater,container,false)
        //vmodel
        val repository = (requireActivity().application as ContactsApplication).repository
        val factory = ShowContactViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[ShowContactViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setOnClickListeners()
        setupui()
        bindUi()
    }

    private fun setupui() {

    }

    private fun setOnClickListeners() {
        binding.favIv.setOnClickListener{
            lifecycleScope.launch{
                viewModel.updateFavContact()
            }
            it.animate().setDuration(300L).scaleXBy(0.1F).scaleYBy(0.1F).withEndAction {
                it.animate().setDuration(300L).scaleXBy(-0.1F).scaleYBy(-0.1F).start()
            }.start()

        }
        binding.editBtn.setOnClickListener{
//            viewModel.contactWithGroups.value?.contact?.id?.let {
//                findNavController().navigate(ShowContactFragmentDirections.actionShowContactFragmentToAddContactFragment(isPersonalDetail = (args.contactId == PERSONAL_DETAIL_ID),editMode = true, contactId = it))
//            }
        }
        binding.delBtn.setOnClickListener{
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure?")
            .setPositiveButton("yes",DialogInterface.OnClickListener{
                _,_->
                    viewModel.deleteContact(args.contactId)
                    Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
            })
            .setNegativeButton("no",DialogInterface.OnClickListener{
                dialog,_->dialog.cancel()
            })
            .show()
    }

    private fun setObservers() {

    }

    fun bindUi(){
            //updates the details to ui
        val contactDetails = ContactResolverUtil.getContactById(requireContext().contentResolver,args.contactId)
        contactDetails?.let{ contact->
            //load image
            if(contact.photo.isNotEmpty()) binding.photoIv.setImageURI(Uri.parse(contact.photo))
            updateFavView(contact.isFavourite)
            //name
            binding.nameTv.text = contact.name
            //load number
            setTextIfNotEmpty(contact.mobileNumber1,binding.number1Group, binding.number1Tv)
            setTextIfNotEmpty(contact.mobileNumber2,binding.number2Group, binding.number2Tv)
            setTextIfNotEmpty(contact.mobileNumber3,binding.number3Group, binding.number3Tv)

            //load bday
            contact.dob?.let { date ->
                setTextIfNotEmpty(date.toFormattedString(),binding.dobGroup,binding.dobTv)
            }
            //load mail
            setTextIfNotEmpty(contact.mail,binding.mailGroup,binding.mailTv)
            //load groups
            //if(it.groups.isNotEmpty()) setupRecyclerView(it.groups)
        }
    }
    private fun setupRecyclerView(groups: List<com.mk.contacts.model.room.Group>) {
        binding.contactGroupsGroup.isVisible = true
        binding.groupsRv.adapter = adapter.also {
            it.submitList(groups)
        }
    }

    private fun setTextIfNotEmpty(text:String,viewGroup: Group,textView:TextView) {
        if(text.isNotEmpty()) {
            viewGroup.isVisible = true
            textView.text = text
        }
    }

    private fun updateFavView(fav:Boolean) {
        binding.favIv.setImageResource(
            if(fav) R.drawable.star_filled
            else R.drawable.star_empty
        )
    }

}