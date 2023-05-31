package com.nik.diplomapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nik.diplomapp.utils.Constants

@Entity(tableName = Constants.HEAT_TABLE)
class HeatEntity (

    var temperature: Float,

    var time: Int

)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}