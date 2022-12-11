package com.mk.contacts.model.contactProvider

import android.app.Activity
import android.database.Cursor
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.mk.contacts.model.room.Contact
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.OperationApplicationException
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.Profile
import android.provider.ContactsContract.RawContacts
import android.provider.MediaStore
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File

object ContactResolverUtil{
    val TAG = "ContactResolverUtil"
    private val fields = arrayOf(
        Phone._ID,
        Phone.PHOTO_THUMBNAIL_URI,
        Phone.DISPLAY_NAME
    )
    private val profileFields = arrayOf(
        Profile._ID,
        Profile.PHOTO_THUMBNAIL_URI,
        Profile.DISPLAY_NAME
    )
    private val detailedFields = arrayOf(
        Phone._ID,
        Phone.PHOTO_THUMBNAIL_URI,
        Phone.DISPLAY_NAME,
        Phone.NUMBER,
        Phone.STARRED
    )

    //gets info's using cursor
    fun getId(cursor: Cursor):Int{
        return cursor.getColumnIndex(Phone._ID).let {
            cursor.getInt(it)?:-1
        }
    }


    //returns the uri of the photo nullable
    fun getPhoto(cursor: Cursor):String{
        return cursor.getColumnIndex(Phone.PHOTO_THUMBNAIL_URI).let {
            cursor.getString(it)?:""
        }
    }
    fun getName(cursor: Cursor):String{
        return cursor.getColumnIndex(Phone.DISPLAY_NAME).let {
            cursor.getString(it)?:"No name"
        }
    }
    fun getNumber(cursor: Cursor):String{
        return cursor.getColumnIndex(Phone.NUMBER).let {
            cursor.getString(it)?:""
        }
    }


    fun getStringData(cursor: Cursor,col:String):String?{
        return cursor.getColumnIndex(col).let {
            cursor.getString(it)
        }
    }
    fun getIntData(cursor: Cursor,col:String):Int?{
        return cursor.getColumnIndex(col).let {
            cursor.getInt(it)
        }
    }


    fun getContactById(contentResolver: ContentResolver,id:Long):Contact?{
        var contact:Contact? = null
        //filter
        val selection = "${Phone._ID} = ?"
        val selectionArgs = arrayOf<String>(id.toString())

        getContactsCursor(contentResolver = contentResolver,
            fields = detailedFields,
            selection = selection,
            selectionArgs = selectionArgs)?.let {

            if(it.count==0) return null

            it.moveToFirst()
            val name = getName(it)
            val number = getNumber(it)
            val fav = getIntData(it,Data.STARRED)?:0
            val photo = getPhoto(it)?:""
            //close cursor
            it.close()
            contact = Contact(id = id,name=name,photo=photo, mobileNumber1 = number, isFavourite = (fav==1))
        }
        return contact
    }

    fun getPersonalDetails(contentResolver:ContentResolver):Contact?{
        var contact:Contact? = null
        getContactsCursor(contentResolver=contentResolver,contentUri = Profile.CONTENT_URI, fields = profileFields)?.let {
            if(it.count!=0){
                it.moveToFirst()
                val id = getIntData(it,Profile._ID)?:-1
                val name = getStringData(it,Profile.DISPLAY_NAME)?:"no name"
                val photo = getStringData(it,Profile.PHOTO_THUMBNAIL_URI)?:""
                it.close()
                contact = Contact(id = id.toLong(),name = name,photo = photo)
            }
        }

        return contact

    }

    fun getContactsCursor(contentResolver:ContentResolver,fields:Array<String> = this.fields
                          ,contentUri:Uri = Phone.CONTENT_URI
                          ,selection:String?=null
                          ,selectionArgs:Array<String>?=null):Cursor?
    {
        //getting cursor
        return contentResolver.query(
            contentUri,
            fields,
            selection,
            selectionArgs,
            Phone.DISPLAY_NAME)
    }
    fun getSearchCursor(searchText:String,contentResolver: ContentResolver):Cursor?{
        val selection = "${Phone.DISPLAY_NAME} like '%"+searchText+"%' OR ${Phone.NUMBER} like '"+searchText+"%'"
        return getContactsCursor(contentResolver = contentResolver, selection = selection)
    }


    fun insertContact(activity: Activity,contact: Contact){
        val contactProviderOperations = arrayListOf<ContentProviderOperation>()
        contactProviderOperations.add(
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE,null)
                .withValue(RawContacts.ACCOUNT_NAME,null).build()
        )
        contactProviderOperations.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID,0)
                .withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME,contact.name)
                .build()
        )
        contactProviderOperations.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID,0)
                .withValue(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER,contact.mobileNumber1)
                .withValue(Phone.TYPE,Phone.TYPE_MOBILE)
                .build()
        )
        if(contact.mobileNumber2.isNotEmpty())
            contactProviderOperations.add(
                ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID,0)
                    .withValue(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER,contact.mobileNumber2)
                    .withValue(Phone.TYPE,Phone.TYPE_HOME)
                    .build()
        )
        if(contact.mobileNumber3.isNotEmpty())
            contactProviderOperations.add(
                ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID,0)
                    .withValue(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER,contact.mobileNumber3)
                    .withValue(Phone.TYPE,Phone.TYPE_WORK)
                    .build()
            )
        if(contact.mail.isNotEmpty())
            contactProviderOperations.add(
                ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID,0)
                    .withValue(Data.MIMETYPE,CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(CommonDataKinds.Email.DATA,contact.mail)
                    .withValue(CommonDataKinds.Email.TYPE,CommonDataKinds.Email.TYPE_WORK)
                    .build()
            )
        if(contact.photo.isNotEmpty()) {
            imagePathToByteArray(activity,contact.photo)?.let {
                contactProviderOperations.add(
                    ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(RawContacts.Data.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(CommonDataKinds.Photo.PHOTO,it)
                        .build()
                )
            }
        }


        try {
            activity.contentResolver.applyBatch(ContactsContract.AUTHORITY,contactProviderOperations)
        }
        catch (e: OperationApplicationException){
            e.printStackTrace()
        }
    }

    private fun imagePathToByteArray(activity: Activity, path: String): ByteArray? {
        var byteArray:ByteArray? = null
        val uri = Uri.fromFile(File(path))
        val byteArrayOutputStream = ByteArrayOutputStream()
        try  {
            val bitmap = if(Build.VERSION.SDK_INT <=28)
                MediaStore.Images.Media.getBitmap(activity.contentResolver,uri)
            else
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(File(path)))

            bitmap?.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream)
            byteArray = byteArrayOutputStream.toByteArray()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return byteArray
    }

    fun deleteContact(){

    }

}
