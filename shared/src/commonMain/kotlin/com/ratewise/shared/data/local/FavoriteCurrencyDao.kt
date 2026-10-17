package com.ratewise.shared.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCurrencyDao {
    @Query("SELECT code FROM favorite_currencies ORDER BY code")
    fun observeAll(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteCurrencyEntity)

    @Query("DELETE FROM favorite_currencies WHERE code = :code")
    suspend fun delete(code: String)
}
