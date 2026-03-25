package com.shopmanager.adapter
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopmanager.R
import com.shopmanager.data.entities.Product

class ProductAdapter(private val onEdit: (Product) -> Unit, private val onDelete: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.VH>() {
    private var items = listOf<Product>()
    fun submitList(list: List<Product>) { items = list; notifyDataSetChanged() }
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvProdName)
        val tvType: TextView = v.findViewById(R.id.tvProdType)
        val tvBuy: TextView = v.findViewById(R.id.tvProdBuy)
        val tvSell: TextView = v.findViewById(R.id.tvProdSell)
        val btnEdit: android.widget.ImageButton = v.findViewById(R.id.btnEditProduct)
        val btnDel: android.widget.ImageButton = v.findViewById(R.id.btnDeleteProduct)
    }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_product, p, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val p = items[pos]
        h.tvName.text = p.name
        h.tvType.text = p.type
        h.tvBuy.text = "Buy: ₹${p.buyPrice}"
        h.tvSell.text = "Sell: ₹${p.sellPrice}"
        h.btnEdit.setOnClickListener { onEdit(p) }
        h.btnDel.setOnClickListener { onDelete(p) }
    }
}
