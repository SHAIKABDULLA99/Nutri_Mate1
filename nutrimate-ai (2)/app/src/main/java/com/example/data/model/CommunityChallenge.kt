package com.example.data.model

data class CommunityChallenge(
    val id: String,
    val titleEn: String,
    val titleTe: String,
    val descEn: String,
    val descTe: String,
    val icon: String,
    val targetDays: Int,
    val completedDays: Int = 0,
    val participantsCount: Int,
    val category: String, // Nutrition, Hydration, Habits
    val badgeNameEn: String,
    val badgeNameTe: String,
    val isJoined: Boolean = false,
    val isCheckedInToday: Boolean = false
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val scorePoints: Int,
    val streakDays: Int,
    val badge: String
)
