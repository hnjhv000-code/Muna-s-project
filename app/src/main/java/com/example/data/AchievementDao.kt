package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY completedAt DESC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement): Long

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("DELETE FROM achievements WHERE id = :id")
    suspend fun deleteAchievementById(id: Int)

    @Query("DELETE FROM achievements")
    suspend fun clearAllAchievements()

    @Query("SELECT * FROM achievements WHERE type = 'HUMAN'")
    suspend fun getHumanAchievements(): List<Achievement>
}
