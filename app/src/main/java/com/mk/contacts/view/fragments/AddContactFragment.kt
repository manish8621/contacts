package com.mk.contacts.view.fragments

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mk.contacts.ContactsApplication
import com.mk.contacts.R
import com.mk.contacts.databinding.FragmentAddContactBinding
import com.mk.contacts.model.room.Group
import com.mk.contacts.viewmodel.AddContactViewModel
import com.mk.contacts.viewmodel.AddContactViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mk.contacts.databinding.GroupListItemLayoutBinding
import com.mk.contacts.databinding.MultipleOptionItemLayoutBinding
import com.mk.contacts.model.contactProvider.ContactResolverUtil
import com.mk.contacts.view.adapter.GROUP_OPTION_VIEW_TYPE
import com.mk.contacts.view.adapter.GroupAdapter
import com.mk.contacts.view.getDate
import kotlinx.coroutines.delay

//TODO:Slow image load
//TODO:save btn to menu
class AddContactFragment : Fragment() {

    lateinit var binding :FragmentAddContactBinding
    lateinit var viewModel: AddContactViewModel
    private val args:AddContactFragmentArgs by navArgs()

    private val adapter = GroupAdapter(viewType = GROUP_OPTION_VIEW_TYPE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddContactBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //vmodel
        val repository = (requireActivity().application as ContactsApplication).repository
        val factory = AddContactViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[AddContactViewModel::class.java]
        binding.viewModel = viewModel
        setObservers()
        setOnClickListeners()
        setupUi()
    }

    private fun setObservers() {
        viewModel.statusText.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearStatusText()
            }
        }
        viewModel.groups.observe(viewLifecycleOwner){
            it?.let {
                adapter.submitList(it)
            }
        }
    }

    private fun setupUi() {
        if(args.isPersonalDetail){
            with(binding){
                groupsLabel.visibility = View.GONE
                groupsRv.visibility = View.GONE
                isFavCb.visibility = View.GONE
            }
            //special id for personal contact details
            viewModel.contact.value?.id = -1
        }
        //if in edit mode fill the fields with details and change ui
        if(args.editMode){
            binding.saveBtn.text ="update"
            viewModel.getContactWithGroups(if (args.isPersonalDetail) -1 else args.contactId)
        }
        //when adding personal detail hide some fields


        binding.groupsRv.adapter = adapter
    }

    private fun setOnClickListeners() {
        adapter.setOnClickListener(object :GroupAdapter.ClickListeners{
            //changing the background color will give the effect of select and unselect
            override fun onGroupOptionItemClickListener(group: Group,binding: MultipleOptionItemLayoutBinding) {
                if(binding.rootLayout.tag == "unchecked") {
                    binding.rootLayout.tag = "checked"
                    binding.groupNameTv.setBackgroundColor(binding.root.context.getColor(R.color.primary))
                    binding.groupNameTv.setTextColor(binding.root.context.getColor(R.color.white))
                    viewModel.addGroup(group)
                }
                else
                {
                    binding.rootLayout.tag = "unchecked"
                    binding.groupNameTv.setBackgroundColor(binding.root.context.getColor(R.color.white))
                    binding.groupNameTv.setTextColor(binding.root.context.getColor(R.color.on_primary))
                    viewModel.removeGroup(group)
                }
            }

            override fun onGroupItemClickListener(group: Group,binding: GroupListItemLayoutBinding) {
            }
        })
        binding.dobEt.setOnClickListener{
            val datePicker = DatePicker(requireContext()).also {
                it.maxDate = Date().time
            }
            AlertDialog.Builder(requireContext()).setView(datePicker)
                .setTitle("date of birth")
                .setPositiveButton("ok"){
                    _,_->
                    viewModel.contact.value?.dob = datePicker.getDate()
                    setDateOfBirth()
                }.setNegativeButton("clear"){
                    dialog,_-> dialog.cancel()
                    viewModel.contact.value?.dob = null
                }
                .show()
        }
        binding.cameraIv.setOnClickListener{

                ImagePicker.with(this@AddContactFragment)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
        }
        binding.saveBtn.setOnClickListener{

            val name = binding.nameEt.text.toString()
            if(name.isEmpty() || name.isBlank()) {
                Toast.makeText(context, "enter a name to save", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(args.editMode)
                updateContact()
            else
                addContact()

            lifecycleScope.launch(Dispatchers.Main){
                delay(200L);
                findNavController().navigateUp()
            }
        }

    }

    private fun updateContact() {

    }

    //adds contact by using util class
    private fun addContact() {
        viewModel.contact.value?.let {
            ContactResolverUtil.insertContact(requireActivity(),it)
            Toast.makeText(context, "Contact saved", Toast.LENGTH_SHORT).show()
        }
    }

    //for getting image uri from file
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "got the image", Toast.LENGTH_SHORT).show()
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
                fileUri?.let{ uri->
//                    viewModel.contactWithGroups.value?.let {
//                        val contact = it.contact.copy(photo = uri.toFile().path)
//                        viewModel.contactWithGroups.postValue(it.copy(contact=contact))
//                    }
                    viewModel.contact.value?.photo = uri.toFile().path
                    binding.photoIv.setImageURI(uri)
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(context, "error while loading image", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "image pick action cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    private fun setDateOfBirth() {
        viewModel.contact.value?.dob?.let {
            binding.dobEt.setText(android.text.format.DateFormat.format("dd/MM/yyyy",it))
        }
    }


}