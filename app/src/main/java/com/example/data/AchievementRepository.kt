package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class AchievementRepository(private val dao: AchievementDao) {
    val allAchievements: Flow<List<Achievement>> = dao.getAllAchievements()

    suspend fun insertAchievement(
        type: String,
        subType: String,
        title: String,
        durationMinutes: Int
    ): Long {
        // Family logic for Humans
        var parentManId: Int? = null
        var parentWomanId: Int? = null
        var growthStage = 1

        if (type == "HUMAN") {
            val humans = dao.getHumanAchievements()
            val men = humans.filter { it.subType == "MAN" }
            val women = humans.filter { it.subType == "WOMAN" }
            val children = humans.filter { it.subType == "CHILD" }

            if (subType == "CHILD") {
                // Link to most recent Man and Woman if available
                if (men.isNotEmpty() && women.isNotEmpty()) {
                    parentManId = men.last().id
                    parentWomanId = women.last().id
                }
            } else if (subType == "MAN") {
                // If a new Man is added and there are existing children, mature the oldest young child!
                for (child in children) {
                    if (child.growthStage < 3) {
                        val updated = child.copy(growthStage = child.growthStage + 1)
                        dao.updateAchievement(updated)
                        break
                    }
                }
            }
        }

        // Random coordinates spread on the virtual agricultural land
        val posX = Random.nextFloat() * 0.85f + 0.075f
        val posY = Random.nextFloat() * 0.85f + 0.075f

        val achievement = Achievement(
            type = type,
            subType = subType,
            title = title,
            durationMinutes = durationMinutes,
            growthStage = growthStage,
            parentManId = parentManId,
            parentWomanId = parentWomanId,
            posX = posX,
            posY = posY
        )

        return dao.insertAchievement(achievement)
    }

    suspend fun deleteAchievement(id: Int) {
        dao.deleteAchievementById(id)
    }

    suspend fun clearAll() {
        dao.clearAllAchievements()
    }
}
