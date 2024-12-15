package com.example.appmenumakanan

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmenumakanan.Model.MenuApiResponse
import com.example.appmenumakanan.Network.RetrofitClient
import com.example.appmenumakanan.database.AppDatabase
import com.example.appmenumakanan.databinding.ActivityEditMenuBinding
import com.example.appmenumakanan.model.Menu
import kotlinx.coroutines.launch

class EditMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMenuBinding
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menu = intent.getParcelableExtra("menu")!!

        val categories = resources.getStringArray(R.array.category_array)
        val spinnerAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categories)

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter

        binding.etMenuName.setText(menu.name)
        binding.etMenuPrice.setText(menu.price.toString())
        binding.etMenuDescription.setText(menu.description)
        binding.etMenuPhotoUrl.setText(menu.photoUrl)

        binding.btnUpdateMenu.setOnClickListener {
            val name = binding.etMenuName.text.toString()
            val price =
                binding.etMenuPrice.text.toString().toDoubleOrNull()
            val description = binding.etMenuDescription.text.toString()
            val photoUrl = binding.etMenuPhotoUrl.text.toString()
            val category = binding.spinnerCategory.selectedItem.toString()

            if (name.isEmpty() || price == null || description.isEmpty() ||
                photoUrl.isEmpty()) {
                Toast.makeText(this, "Please fill all fields",
                    Toast.LENGTH_SHORT).show()
            } else {
                val updatedMenu = menu.copy(
                    name = name,
                    price = price,
                    description = description,
                    photoUrl = photoUrl,
                    category = category
                )

                lifecycleScope.launch {
                    try {
                        val menuApiResponse = MenuApiResponse(
                            name = updatedMenu.name,
                            price = updatedMenu.price,
                            description = updatedMenu.description,
                            photoUrl = updatedMenu.photoUrl,
                            category = updatedMenu.category
                        )

                        val response =
                            RetrofitClient.apiService.updateMenu(updatedMenu.id, menuApiResponse)
                        if (response.isSuccessful) {

                            AppDatabase.getInstance(this@EditMenuActivity).menuDao().updateMenu(updatedMenu)
                            Toast.makeText(this@EditMenuActivity, "Menu updated successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                        } else {
                            Toast.makeText(this@EditMenuActivity, "Failed to update menu", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@EditMenuActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}