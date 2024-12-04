package com.example.gospace_ipt

data class User(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val gender: String = "",
    val role: String = "",
    val img: String = ""
    
)

data class RoomList(
    val roomName: String = "",
    val status: String = "Available",
    val borrower: String = "None",
    val role: String = "Faculty",
    val schedule: String = "None" ,
    val message: String = "None"
)