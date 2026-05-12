package com.jatrenammapride.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jatrenammapride.data.model.LostItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LostItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LostItem): Long

    @Update
    suspend fun updateItem(item: LostItem)

    @Delete
    suspend fun deleteItem(item: LostItem)

    @Query("SELECT * FROM lost_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<LostItem>>

    @Query("SELECT * FROM lost_items WHERE isResolved = 0 ORDER BY createdAt DESC")
    fun getActiveItems(): Flow<List<LostItem>>

    @Query("SELECT * FROM lost_items WHERE id = :id")
    fun getItemById(id: Int): Flow<LostItem?>
}
