package com.jbc.roomdb.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,

    // Clave primaria
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
