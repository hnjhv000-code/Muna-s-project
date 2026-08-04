package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "TREE", "FLOWER", "PALACE", "HUMAN"
    val subType: String, // "TREE_OAK", "FLOWER_ROSE", "PALACE_GOLDEN", "MAN", "WOMAN", "CHILD"
    val title: String,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val growthStage: Int = 1, // 1: Infant/Young, 2: Youth/Medium, 3: Adult/Full
    val parentManId: Int? = null,
    val parentWomanId: Int? = null,
    val posX: Float = 0.5f, // 0.0 to 1.0 normalized position on land
    val posY: Float = 0.5f
)
