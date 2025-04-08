package com.jbc.roomdb.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jbc.roomdb.data.Contact

@Database(
    entities = [Contact::class],
    version = 1
)
abstract class ContactDatabase: RoomDatabase() {
    abstract val dao: ContactDAO
}