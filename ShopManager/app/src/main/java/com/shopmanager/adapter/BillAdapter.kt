package com.shopmanager.adapter
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopmanager.R
import com.shopmanager.data.entities.Bill
import java.text.SimpleDateFormat
import java.util.*

class BillAdapter(private val onClick: (Bill) -> Unit) : RecyclerView.Adapter<BillAdapter.VH>() {
    private var items = listOf<Bill>()
    fun submitList(list: List<Bill>) { items = list; notifyDataSetChanged() }
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvCustomer: TextView = v.findViewById(R.id.tvBillCustomer)
        val tvDate: TextView = v.findViewById(R.id.tvBillDate)
        val tvAmount: TextView = v.findViewById(R.id.tvBillAmount)
        val tvStatus: TextView = v.findViewById(R.id.tvBillStatus)
        val tvBillNo: TextView = v.findViewById(R.id.tvBillNo)
    }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_bill, p, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val bill = items[pos]
        h.tvCustomer.text = bill.customerName
        h.tvDate.text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(bill.createdAt))
        h.tvAmount.text = "₹${String.format("%.2f", bill.totalAmount)}"
        h.tvStatus.text = bill.status
        h.tvStatus.setTextColor(h.itemView.context.getColor(if (bill.status == "DRAFT") android.R.color.holo_orange_dark else android.R.color.holo_green_dark))
        h.tvBillNo.text = "#${bill.id}"
        h.itemView.setOnClickListener { onClick(bill) }
    }
}
