package com.example.appmenumakanan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appmenumakanan.Model.MenuApiResponse
import com.example.appmenumakanan.Network.RetrofitClient
import com.example.appmenumakanan.database.AppDatabase
import com.example.appmenumakanan.databinding.ActivityMainBinding
import com.example.appmenumakanan.model.Menu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: MenuAdapter
    private lateinit var preferences: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = PreferencesHelper(this)

        if (!preferences.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        database = AppDatabase.getInstance(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MenuAdapter(emptyList(), ::onEditMenu, ::onDeleteMenu)
        binding.recyclerView.adapter = adapter

        setupSpinner()
        loadDataFromApi()

        binding.btnAccount.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        binding.btnAddMenu.setOnClickListener {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSpinner() {
        val categories = resources.getStringArray(R.array.category_array)
        val spinnerAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categories)

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter

        binding.spinnerCategory.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view:
            View?, position: Int, id: Long) {
                val selectedCategory =
                    parent?.getItemAtPosition(position).toString()
                filterMenusByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filterMenusByCategory(category: String) {
        lifecycleScope.launch {
            val menus = if (category == "All") {
                withContext(Dispatchers.IO) {
                    database.menuDao().getAllMenus() }
            } else {
                withContext(Dispatchers.IO) {
                    database.menuDao().getMenusByCategory(category) }
            }
            adapter.updateData(menus.distinctBy { it.name })
        }
    }

    private fun loadDataFromApi() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMenus() }

                if (response.isSuccessful && response.body() != null) {
                    val apiMenus = response.body()!!

                    val menus = apiMenus.map { apiMenu ->
                        Menu(
                            name = apiMenu.name,
                            price = apiMenu.price,
                            photoUrl = apiMenu.photoUrl,
                            description = apiMenu.description,
                            category = apiMenu.category
                        )
                    }

                    withContext(Dispatchers.IO) {
                        val existingMenus =
                            database.menuDao().getAllMenus()

                        val newMenus = menus.filter { apiMenu ->
                            existingMenus.none { it.name == apiMenu.name }
                        }

                        if (newMenus.isNotEmpty()) {
                            database.menuDao().insertAll(newMenus)
                        }
                    }

                    adapter.updateData(menus.distinctBy { it.name })
                } else {
                    Toast.makeText(this@MainActivity, "Gagal memuat data dari API.", Toast.LENGTH_SHORT).show()

                        val cachedMenus = withContext(Dispatchers.IO) {
                        database.menuDao().getAllMenus() }
                    adapter.updateData(cachedMenus.distinctBy { it.name })
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()

                val cachedMenus = withContext(Dispatchers.IO) {
                    database.menuDao().getAllMenus() }
                adapter.updateData(cachedMenus.distinctBy { it.name })
            }
        }
    }

    private fun addMenu(menu: MenuApiResponse) {
        lifecycleScope.launch {
            val response = RetrofitClient.apiService.addMenu(menu)

            if (response.isSuccessful) {
                Toast.makeText(this@MainActivity, "Menu added successfully", Toast.LENGTH_SHORT).show()
                loadDataFromApi()
            } else {
                Toast.makeText(this@MainActivity, "Failed to add menu",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onEditMenu(menu: Menu) {
        val intent = Intent(this, EditMenuActivity::class.java)
        intent.putExtra("menu", menu)
        startActivity(intent)
    }

    private fun onDeleteMenu(menu: Menu) {
        lifecycleScope.launch {
            try {
                val response =
                    RetrofitClient.apiService.deleteMenu(menu.id)

                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Menu deleted successfully", Toast.LENGTH_SHORT).show()
                            loadDataFromApi()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to delete menu", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error deleting menu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}