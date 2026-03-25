package com.shopmanager.utils
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.shopmanager.data.entities.Bill
import com.shopmanager.data.entities.BillItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {
    fun generateBillPdf(context: Context, bill: Bill, items: List<BillItem>, shopName: String, shopType: String): File {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas

        val bold = Paint().apply { typeface = Typeface.DEFAULT_BOLD; textSize = 18f; color = Color.BLACK }
        val normal = Paint().apply { typeface = Typeface.DEFAULT; textSize = 13f; color = Color.BLACK }
        val small = Paint().apply { typeface = Typeface.DEFAULT; textSize = 11f; color = Color.DKGRAY }
        val line = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
        val header = Paint().apply { typeface = Typeface.DEFAULT_BOLD; textSize = 13f; color = Color.WHITE }
        val headerBg = Paint().apply { color = Color.rgb(63,81,181) }
        val altRow = Paint().apply { color = Color.rgb(240,242,255) }

        var y = 50f

        // Shop Header
        bold.textSize = 22f; bold.textAlign = Paint.Align.CENTER
        canvas.drawText(shopName, 297f, y, bold)
        y += 25f
        small.textAlign = Paint.Align.CENTER
        canvas.drawText(shopType, 297f, y, small)
        y += 20f
        canvas.drawLine(40f, y, 555f, y, line)
        y += 20f

        // Bill Info
        bold.textSize = 14f; bold.textAlign = Paint.Align.LEFT
        canvas.drawText("INVOICE", 40f, y, bold)
        normal.textAlign = Paint.Align.RIGHT
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(bill.createdAt))
        canvas.drawText(dateStr, 555f, y, normal)
        y += 18f
        small.textAlign = Paint.Align.LEFT
        canvas.drawText("Bill #${bill.id}", 40f, y, small)
        y += 18f
        canvas.drawText("Customer: ${bill.customerName}", 40f, y, small)
        y += 20f
        canvas.drawLine(40f, y, 555f, y, line)
        y += 15f

        // Table header
        canvas.drawRect(40f, y - 15f, 555f, y + 10f, headerBg)
        header.textAlign = Paint.Align.LEFT
        canvas.drawText("Item", 45f, y, header)
        canvas.drawText("Type", 230f, y, header)
        header.textAlign = Paint.Align.CENTER
        canvas.drawText("Qty", 360f, y, header)
        header.textAlign = Paint.Align.RIGHT
        canvas.drawText("Rate", 450f, y, header)
        canvas.drawText("Amount", 550f, y, header)
        y += 20f

        // Table rows
        items.forEachIndexed { idx, item ->
            if (idx % 2 == 0) canvas.drawRect(40f, y - 14f, 555f, y + 6f, altRow)
            normal.textAlign = Paint.Align.LEFT; normal.textSize = 12f
            canvas.drawText(item.productName.take(22), 45f, y, normal)
            canvas.drawText(item.productType.take(14), 230f, y, normal)
            normal.textAlign = Paint.Align.CENTER
            canvas.drawText(item.quantity.toString(), 360f, y, normal)
            normal.textAlign = Paint.Align.RIGHT
            canvas.drawText("₹${String.format("%.2f",item.sellPrice)}", 450f, y, normal)
            canvas.drawText("₹${String.format("%.2f",item.quantity * item.sellPrice)}", 550f, y, normal)
            y += 22f
        }

        y += 5f
        canvas.drawLine(40f, y, 555f, y, line)
        y += 20f

        // Total
        bold.textSize = 16f; bold.textAlign = Paint.Align.RIGHT
        canvas.drawText("Total: ₹${String.format("%.2f", bill.totalAmount)}", 555f, y, bold)
        y += 40f

        // Footer
        small.textAlign = Paint.Align.CENTER
        canvas.drawText("Thank you for your business!", 297f, y, small)

        doc.finishPage(page)
        val file = File(context.getExternalFilesDir(null), "Bill_${bill.id}_${System.currentTimeMillis()}.pdf")
        doc.writeTo(file.outputStream())
        doc.close()
        return file
    }
}
