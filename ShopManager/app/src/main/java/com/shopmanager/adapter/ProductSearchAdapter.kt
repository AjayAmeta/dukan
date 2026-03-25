package com.shopmanager.adapter
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopmanager.R
import com.shopmanager.data.entities.Product

class ProductSearchAdapter(private val onClick: (Product) -> Unit) : RecyclerView.Adapter<ProductSearchAdapter.VH>() {
    private var items = listOf<Product>()
    fun submitList(list: List<Product>) { items = list; notifyDataSetChanged() }
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvProductName)
        val tvType: TextView = v.findViewById(R.id.tvProductType)
        val tvPrice: TextView = v.findViewById(R.id.tvProductPrice)
    }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_product_search, p, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val p = items[pos]
        h.tvName.text = p.name
        h.tvType.text = p.type
        h.tvPrice.text = "₹${p.sellPrice}"
        h.itemView.setOnClickListener { onClick(p) }
    }
}
