package com.nik.diplomapp.data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nik.diplomapp.utils.Constants

@Entity(tableName = Constants.PROFILES_TABLE)
class ProfileEntity (

    var name: String,

    var temperature: Int = 1,

    var power: Int = 1

)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}