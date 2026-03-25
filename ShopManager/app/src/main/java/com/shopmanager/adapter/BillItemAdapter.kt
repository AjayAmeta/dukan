package com.shopmanager.adapter
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.shopmanager.R
import com.shopmanager.data.entities.BillItem

class BillItemAdapter(private val onRemove: (Int) -> Unit) : RecyclerView.Adapter<BillItemAdapter.VH>() {
    private val items = mutableListOf<BillItem>()
    fun getItems() = items.toList()
    fun addItem(item: BillItem) { items.add(item); notifyItemInserted(items.size - 1) }
    fun removeItem(pos: Int) { items.removeAt(pos); notifyItemRemoved(pos) }
    fun clear() { items.clear(); notifyDataSetChanged() }
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvItemName)
        val tvQty: TextView = v.findViewById(R.id.tvItemQty)
        val tvSubtotal: TextView = v.findViewById(R.id.tvItemSubtotal)
        val btnMinus: ImageButton = v.findViewById(R.id.btnMinus)
        val btnPlus: ImageButton = v.findViewById(R.id.btnPlus)
        val btnRemove: ImageButton = v.findViewById(R.id.btnRemove)
    }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_bill_item, p, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = items[pos]
        h.tvName.text = item.productName
        h.tvQty.text = item.quantity.toString()
        h.tvSubtotal.text = "₹${String.format("%.2f", item.quantity * item.sellPrice)}"
        h.btnPlus.setOnClickListener {
            val updated = item.copy(quantity = item.quantity + 1)
            items[h.adapterPosition] = updated
            notifyItemChanged(h.adapterPosition)
        }
        h.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                val updated = item.copy(quantity = item.quantity - 1)
                items[h.adapterPosition] = updated
                notifyItemChanged(h.adapterPosition)
            }
        }
        h.btnRemove.setOnClickListener { onRemove(h.adapterPosition) }
    }
}
