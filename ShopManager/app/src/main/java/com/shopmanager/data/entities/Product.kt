package com.shopmanager.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shopId: Int,
    val name: String,
    val type: String,
    val buyPrice: Double,
    val sellPrice: Double,
    val createdAt: Long = System.currentTimeMillis()
)
