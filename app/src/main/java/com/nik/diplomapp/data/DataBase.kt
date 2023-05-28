package com.nik.diplomapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nik.diplomapp.data.entities.ProfileEntity

@Database(
    entities = [ProfileEntity::class],
    version = 1,
    exportSchema = false
)

abstract class DataBase: RoomDatabase() {

    abstract fun appDao(): AppDAO

}