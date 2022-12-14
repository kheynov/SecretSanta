package ru.kheynov.secretsanta.domain.entities

data class RoomItem(
    val roomId: String,
    val roomName: String,
    val membersCount: String,
    val gameState: String,
    val date: String,
    var gameStateColor: Int? = null,
)