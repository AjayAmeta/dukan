package com.shopmanager.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shopId: Int,
    val customerName: String,
    val status: String,
    val totalAmount: Double,
    val totalProfit: Double,
    val createdAt: Long = System.currentTimeMillis()
)
