package com.nik.diplomapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nik.diplomapp.data.Entities.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profileEntity: ProfileEntity)

    @Query("SELECT * FROM profiles_table ORDER BY id ASC")
    fun readProfiles(): Flow<List<ProfileEntity>>
}