package com.mk.contacts.view.adapter

import android.database.Cursor
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.mk.contacts.R
import com.mk.contacts.databinding.ContactListItemBinding
import com.mk.contacts.model.contactProvider.ContactResolverUtil
import com.mk.contacts.model.room.Contact

class ContactsListAdapter(private var cursor: Cursor) :RecyclerView.Adapter<ContactsListAdapter.ItemViewHolder>() {
    val TAG = "ContactsListAdapter"
    private var itemClickListener :((contactId:Long)->Unit)? = null

    class ItemViewHolder(private val binding: ContactListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
            contact: Contact,
            itemClickListener: ((contactId: Long) -> Unit)?
        ){

            binding.contact = contact

            binding.root.setOnClickListener{
                itemClickListener?.invoke(+contact.id)
            }
        }
        companion object{
            fun from(parent: ViewGroup):ItemViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ContactListItemBinding.inflate(layoutInflater,parent,false)

                return ItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if(position == -1) return

        //move cursor
        cursor.moveToPosition(position)

        //get details from cursor
        val name = ContactResolverUtil.getName(cursor)
        val id = ContactResolverUtil.getId(cursor)
        val photoUri = ContactResolverUtil.getPhoto(cursor)

        //bind
        Contact(id = id.toLong(),name=name, photo = photoUri?:"").let {contact->
            holder.bind(contact,itemClickListener)
        }

    }
    fun setOnClickListener(clickListener :((contactId:Long)->Unit)){
        this.itemClickListener = clickListener
    }

    fun submitCursor(cursor: Cursor) {
        this.cursor = cursor
        notifyDataSetChanged()
    }
    fun notifyDataModified(){
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return cursor.count
    }
}