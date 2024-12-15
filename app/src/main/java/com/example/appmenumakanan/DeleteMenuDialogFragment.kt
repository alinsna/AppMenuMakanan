package com.example.appmenumakanan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.appmenumakanan.databinding.DialogDeleteMenuBinding
import com.example.appmenumakanan.model.Menu

class DeleteMenuDialogFragment(private val menu: Menu, private val
onDelete: () -> Unit) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogDeleteMenuBinding.inflate(inflater, container,
            false)
        binding.tvMenuName.text = "Are you sure you want to delete ${menu.name}?"

        binding.btnYes.setOnClickListener {
            onDelete()
            dismiss()
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}