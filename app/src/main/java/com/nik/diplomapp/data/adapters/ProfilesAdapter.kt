package com.nik.diplomapp.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nik.diplomapp.R
import com.nik.diplomapp.data.entities.ProfileEntity

class ProfilesAdapter (private val listener: OnItemClickListener) : RecyclerView.Adapter<ProfilesAdapter.ProfilesAdapterViewHolder>(){

    private var profilesList = ArrayList<ProfileEntity>()

    class ProfilesAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val profileTemperature: TextView = itemView.findViewById(R.id.temperatureProfile)
        val profilePower: TextView = itemView.findViewById(R.id.powerProfile)
        val profileName: TextView = itemView.findViewById(R.id.nameProfile)
        val activateBtn: ImageView = itemView.findViewById(R.id.activate_btn)
        val deleteBtn: ImageView = itemView.findViewById(R.id.delete_btn)
        val editBtn: ImageView = itemView.findViewById(R.id.edit_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilesAdapterViewHolder {
        return ProfilesAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.profiles_row_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ProfilesAdapterViewHolder, position: Int) {
        holder.profileTemperature.text = profilesList[position].temperature.toString() + " °C"
        holder.profilePower.text = profilesList[position].power.toString() + " W"
        holder.profileName.text = profilesList[position].name

        holder.activateBtn.setOnClickListener {
            listener.onActivateClick(profilesList[position])
        }

        holder.editBtn.setOnClickListener {
            listener.onEditClick(position, profilesList[position].name)
        }

        holder.deleteBtn.setOnClickListener {
            listener.onDeleteClick(position, profilesList[position].name)
        }
    }

    override fun getItemCount(): Int {
        return profilesList.size
    }

    interface OnItemClickListener {
        fun onActivateClick(profileEntity: ProfileEntity)
        fun onEditClick(position: Int, name : String)
        fun onDeleteClick(position: Int, name : String)
    }

    fun setData(newData: List<ProfileEntity>) {
        profilesList.clear()
        profilesList.addAll(newData)
        notifyDataSetChanged()
    }
}