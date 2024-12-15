package com.example.appmenumakanan

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appmenumakanan.databinding.ItemMenuBinding
import com.example.appmenumakanan.Network.RetrofitClient
import com.example.appmenumakanan.database.AppDatabase
import com.example.appmenumakanan.model.Menu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class MenuAdapter(
    private var menus: List<Menu>,
    private val onEditMenu: (Menu) -> Unit,
    private val onDeleteMenu: (Menu) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: Menu) {
            binding.menuName.text = menu.name
            binding.menuPrice.text = formatCurrency(menu.price)
            binding.menuDescription.text = menu.description
            Glide.with(binding.root.context)
                .load(menu.photoUrl)
                .into(binding.menuImage)

            binding.btnEdit.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, EditMenuActivity::class.java)
                intent.putExtra("menu", menu)
                context.startActivity(intent)
            }


            binding.btnDelete.setOnClickListener {
                showDeleteConfirmationDialog(menu)
            }
        }

        private fun formatCurrency(amount: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id",
                "ID")) as DecimalFormat
            val symbols = formatter.decimalFormatSymbols.apply {
                currencySymbol = "Rp."
                groupingSeparator = '.'
                decimalSeparator = ','
            }
            formatter.decimalFormatSymbols = symbols
            return formatter.format(amount)
        }

        private fun showDeleteConfirmationDialog(menu: Menu) {
            val context = binding.root.context
            val dialog = DeleteMenuDialogFragment(menu) {
                deleteMenu(menu)
            }
            dialog.show((context as
                    FragmentActivity).supportFragmentManager, "DeleteMenuDialog")
        }

        private fun deleteMenu(menu: Menu) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.deleteMenu(menu.id)
                    }

                    if (response.isSuccessful) {
                        withContext(Dispatchers.IO) {

                            AppDatabase.getInstance(binding.root.context).menuDao().deleteMenu(menu)
                        }

                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            menus = menus.filter { it.id != menu.id }
                            notifyItemRemoved(position)
                            Toast.makeText(binding.root.context, "Menu deleted", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(binding.root.context, "Failed to delete menu", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(binding.root.context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MenuViewHolder {
        val binding =
            ItemMenuBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menus[position])
    }

    override fun getItemCount(): Int = menus.size

    fun updateData(newMenus: List<Menu>) {
        if (menus != newMenus) {
            menus = newMenus.distinctBy { it.name }
            notifyDataSetChanged()
        }
    }
}