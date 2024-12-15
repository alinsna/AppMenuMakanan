package com.example.appmenumakanan.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.appmenumakanan.model.Menu

@Dao
interface MenuDao {
    @Insert
    fun insertAll(menus: List<Menu>)

    @Query("SELECT * FROM menu")
    fun getAllMenus(): List<Menu>

    @Query("SELECT * FROM menu WHERE category = :category")
    fun getMenusByCategory(category: String): List<Menu>

    @Update
    fun updateMenu(menu: Menu)

    @Delete
    fun deleteMenu(menu: Menu)
}