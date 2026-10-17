package com.ratewise.shared.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_currencies")
data class FavoriteCurrencyEntity(
    @PrimaryKey val code: String,
)
