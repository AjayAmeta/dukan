package com.shopmanager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.shopmanager.data.entities.Shop
import com.shopmanager.databinding.ActivityShopSelectionBinding
import com.shopmanager.utils.PreferenceHelper
import com.shopmanager.viewmodel.MainViewModel

class ShopSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopSelectionBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PreferenceHelper.isShopSelected(this)) {
            startMain(); return
        }
        binding = ActivityShopSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = ShopListAdapter { shop ->
            PreferenceHelper.saveSelectedShop(this, shop.id, shop.name, shop.type)
            startMain()
        }
        binding.recyclerShops.layoutManager = LinearLayoutManager(this)
        binding.recyclerShops.adapter = adapter
        viewModel.allShops.observe(this) { shops ->
            adapter.submitList(shops)
            binding.tvNoShops.visibility = if (shops.isEmpty()) View.VISIBLE else View.GONE
        }
        binding.btnCreateShop.setOnClickListener { showCreateShopDialog() }
    }

    private fun showCreateShopDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_create_shop, null)
        val etName = view.findViewById<TextInputEditText>(R.id.etShopName)
        val etType = view.findViewById<TextInputEditText>(R.id.etShopType)
        AlertDialog.Builder(this)
            .setTitle("Create New Shop")
            .setView(view)
            .setPositiveButton("Create") { _, _ ->
                val name = etName.text.toString().trim()
                val type = etType.text.toString().trim()
                if (name.isEmpty()) { Toast.makeText(this, "Enter shop name", Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                val shop = Shop(name = name, type = type.ifEmpty { "General" })
                viewModel.insertShop(shop) { id ->
                    PreferenceHelper.saveSelectedShop(this, id.toInt(), name, shop.type)
                    startMain()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

class ShopListAdapter(private val onClick: (Shop) -> Unit) : RecyclerView.Adapter<ShopListAdapter.VH>() {
    private var shops = listOf<Shop>()
    fun submitList(list: List<Shop>) { shops = list; notifyDataSetChanged() }
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvShopName)
        val tvType: TextView = v.findViewById(R.id.tvShopType)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false))
    override fun getItemCount() = shops.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val shop = shops[pos]
        h.tvName.text = shop.name
        h.tvType.text = shop.type
        h.itemView.setOnClickListener { onClick(shop) }
    }
}
