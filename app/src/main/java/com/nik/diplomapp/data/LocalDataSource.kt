package com.nik.diplomapp.data

import com.nik.diplomapp.data.entities.ProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val appDAO: AppDAO
) {

    fun readProfiles(): Flow<List<ProfileEntity>> {
        return appDAO.readProfiles()
    }

    suspend fun insertProfiles(profileEntity: ProfileEntity) {
        appDAO.insertProfiles(profileEntity)
    }

    suspend fun deleteFromProfiles(name: String){
        appDAO.deleteProfile(name)
    }

    suspend fun updateProfile(name: String, oldName:String){
        appDAO.updateProfile(name, oldName)
    }
}