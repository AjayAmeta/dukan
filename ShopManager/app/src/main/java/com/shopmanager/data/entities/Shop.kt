package com.shopmanager.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "shops")
data class Shop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val createdAt: Long = System.currentTimeMillis()
)
