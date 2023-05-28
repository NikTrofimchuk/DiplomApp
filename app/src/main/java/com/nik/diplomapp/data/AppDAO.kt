package com.nik.diplomapp.data

import androidx.room.*
import com.nik.diplomapp.data.entities.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profileEntity: ProfileEntity)

    @Query("SELECT * FROM profiles_table ORDER BY id ASC")
    fun readProfiles(): Flow<List<ProfileEntity>>

    @Query("DELETE FROM profiles_table WHERE name = :name")
    suspend fun deleteProfile(name: String)

    @Query("UPDATE profiles_table SET name = :name WHERE name = :oldName")
    suspend fun updateProfile(name: String, oldName: String)
}