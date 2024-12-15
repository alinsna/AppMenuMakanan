package com.example.appmenumakanan

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmenumakanan.Model.MenuApiResponse
import com.example.appmenumakanan.Model.toMenu
import com.example.appmenumakanan.Network.RetrofitClient
import com.example.appmenumakanan.database.AppDatabase
import com.example.appmenumakanan.databinding.ActivityAddMenuBinding
import kotlinx.coroutines.launch

class AddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categories = resources.getStringArray(R.array.category_array)
        val spinnerAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categories)

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter

        binding.btnSaveMenu.setOnClickListener {
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
                val newMenuApiResponse = MenuApiResponse(
                    name = name,
                    price = price,
                    photoUrl = photoUrl,
                    description = description,
                    category = category
                )

                lifecycleScope.launch {
                    val response =
                        RetrofitClient.apiService.addMenu(newMenuApiResponse)
                    if (response.isSuccessful) {
                        val menu = response.body()?.toMenu()
                        menu?.let {

                            AppDatabase.getInstance(this@AddMenuActivity).menuDao().insertAll(listOf(it
                            ))

                            Toast.makeText(this@AddMenuActivity, "Menu added successfully", Toast.LENGTH_SHORT).show()

                            finish()
                        }
                    } else {
                        Toast.makeText(this@AddMenuActivity, "Failed to add menu: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }

    }
}
