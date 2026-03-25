package com.shopmanager.data.entities
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(
    tableName = "bill_items",
    foreignKeys = [ForeignKey(entity = Bill::class, parentColumns = ["id"], childColumns = ["billId"], onDelete = ForeignKey.CASCADE)]
)
data class BillItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val billId: Int,
    val productId: Int,
    val productName: String,
    val productType: String,
    val quantity: Int,
    val buyPrice: Double,
    val sellPrice: Double
)
