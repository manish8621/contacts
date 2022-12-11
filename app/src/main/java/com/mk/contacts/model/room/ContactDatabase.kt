package com.mk.contacts.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mk.contacts.model.room.relations.ContactGroupsCrossRef
import java.util.*

@Database(entities = [Contact::class,Group::class,ContactGroupsCrossRef::class], version = 3, exportSchema = false)
@TypeConverters(DateConverters::class)
abstract class ContactDatabase :RoomDatabase(){
    abstract val contactDao:ContactDao
}

object contactDataBase {
    private var instance: ContactDatabase? = null
    fun getDatabase(context: Context): ContactDatabase {
        return instance ?: Room.databaseBuilder(
            context,
            ContactDatabase::class.java,
            "contacts_database"
        ).addCallback(RoomPrePopulateCallback())
            .fallbackToDestructiveMigration()
            .build()
            .also { instance = it }
    }
}


//to prepopulate room database
class RoomPrePopulateCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL("insert into groups_table(group_name) values('Family');")
            db.execSQL("insert into groups_table(group_name) values('Friend');")
            db.execSQL("insert into groups_table(group_name) values('Colleagues');")
    }
}

class DateConverters{
    @TypeConverter
    fun fromDate(date: Date?):Long?{
        return date?.time
    }
    @TypeConverter
    fun toDate(dateTimeLong:Long?): Date? {
        return dateTimeLong?.let { Date(it) }
    }
}