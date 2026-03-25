package com.shopmanager.ui
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shopmanager.R
import com.shopmanager.adapter.ProductAdapter
import com.shopmanager.data.entities.Product
import com.shopmanager.databinding.FragmentProductsBinding
import com.shopmanager.utils.PreferenceHelper
import com.shopmanager.viewmodel.MainViewModel

class ProductsFragment : Fragment() {
    private var _b: FragmentProductsBinding? = null
    private val b get() = _b!!
    private val vm: MainViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter
    private var allProducts = listOf<Product>()
    private var selectedType = "All"

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentProductsBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        productAdapter = ProductAdapter(onEdit = { showAddEditDialog(it) }, onDelete = { showDeleteConfirm(it) })
        b.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerProducts.adapter = productAdapter

        vm.getProducts().observe(viewLifecycleOwner) { products ->
            allProducts = products
            filterAndDisplay()
        }
        vm.getProductTypes().observe(viewLifecycleOwner) { types ->
            setupTypeChips(listOf("All") + types)
        }

        b.etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterAndDisplay() }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        b.fabAddProduct.setOnClickListener { showAddEditDialog(null) }
    }

    private fun setupTypeChips(types: List<String>) {
        b.chipGroupTypes.removeAllViews()
        types.forEach { type ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = type; isCheckable = true; isChecked = (type == selectedType)
                setOnClickListener { selectedType = type; filterAndDisplay() }
            }
            b.chipGroupTypes.addView(chip)
        }
    }

    private fun filterAndDisplay() {
        val query = b.etSearchProduct.text.toString().trim().lowercase()
        var filtered = if (selectedType == "All") allProducts else allProducts.filter { it.type == selectedType }
        if (query.isNotEmpty()) filtered = filtered.filter { it.name.lowercase().contains(query) || it.type.lowercase().contains(query) }
        productAdapter.submitList(filtered)
        b.tvNoProducts.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showAddEditDialog(existing: Product?) {
        val dlgView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_product, null)
        val etName = dlgView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etProductName)
        val etType = dlgView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etProductType)
        val etBuy = dlgView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBuyPrice)
        val etSell = dlgView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSellPrice)
        existing?.let { etName.setText(it.name); etType.setText(it.type); etBuy.setText(it.buyPrice.toString()); etSell.setText(it.sellPrice.toString()) }
        AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Add Product/Part" else "Edit Product/Part")
            .setView(dlgView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val type = etType.text.toString().trim()
                val buy = etBuy.text.toString().toDoubleOrNull() ?: 0.0
                val sell = etSell.text.toString().toDoubleOrNull() ?: 0.0
                if (name.isEmpty()) { Toast.makeText(requireContext(), "Enter product name", Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                val shopId = PreferenceHelper.getShopId(requireContext())
                if (existing == null) {
                    vm.insertProduct(Product(shopId = shopId, name = name, type = type.ifEmpty { "General" }, buyPrice = buy, sellPrice = sell))
                } else {
                    vm.updateProduct(existing.copy(name = name, type = type.ifEmpty { "General" }, buyPrice = buy, sellPrice = sell))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirm(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Delete '${product.name}'?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteProduct(product) }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
