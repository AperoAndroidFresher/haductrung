package com.example.haductrung.database.entity

import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.annotation.StyleRes
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName ="users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId :Int=0,

    val username: String,
    val passwordHash:String,
    val email:String,

    val phone: String? =null,
    val university :String?=null,
    val description : String?= null,
    val imageUri: String? =null

)