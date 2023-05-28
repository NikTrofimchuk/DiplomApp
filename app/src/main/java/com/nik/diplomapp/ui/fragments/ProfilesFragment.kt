package com.nik.diplomapp.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.nik.diplomapp.MainViewModel
import com.nik.diplomapp.R
import com.nik.diplomapp.data.adapters.ProfilesAdapter
import com.nik.diplomapp.data.entities.ProfileEntity
import com.nik.diplomapp.databinding.FragmentProfilesBinding

class ProfilesFragment : Fragment(), ProfilesAdapter.OnItemClickListener {

    private lateinit var viewModel : MainViewModel

    private val profilesAdapter = ProfilesAdapter(this)
    private var _binding: FragmentProfilesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.profilesRc.adapter = profilesAdapter
        binding.profilesRc.layoutManager = LinearLayoutManager(requireContext())

        viewModel.readProfiles.observe(viewLifecycleOwner) { database ->
            profilesAdapter.setData(database)
            Log.d("database", database.toString())
        }
    }

    override fun onActivateClick(profileEntity: ProfileEntity) {
        findNavController().navigate(R.id.action_navigation_profiles_to_navigation_home)
    }

    override fun onEditClick(position: Int, name: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_text_dialog, null)

        val editText = dialogView.findViewById<TextInputEditText>(R.id.edit_value_text)
        editText.setText(name)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle(R.string.edit_dialog_title)
            .setPositiveButton(R.string.save) { dialog, which ->
                // Обработка нажатия кнопки "Сохранить"
                val editedValue = editText.text.toString()

                viewModel.updateInProfile(editedValue, name)
            }
            .setNegativeButton(R.string.cancel) { dialog, which ->
                // Обработка нажатия кнопки "Отмена"
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onDeleteClick(position: Int, name: String) {
        viewModel.deleteProfile(name)
        Toast.makeText(context, "Шаблон удален", Toast.LENGTH_SHORT).show()
    }
}