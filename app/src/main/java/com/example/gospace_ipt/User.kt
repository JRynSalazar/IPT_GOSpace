package com.example.gospace_ipt

import java.io.Serializable

data class User(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val gender: String = "",
    val role: String = "",
    val img: String = "",
    val roomRequestState: String = ""
    
)

data class RoomList(
    val roomName: String = "",
    val roomType: String = "Normal",
    val status: String = "Available",
    val borrower: String = "None",
    val role: String = "Faculty",
    val schedule: String = "None",
    val message: String = "None",
    val availability: Boolean = true,
    val endTime: Long = 0L,
    val borrowerName: String? = null,
    val scheduleText: String? = null ,
    val state: String = "Available"
)


data class RoomMng(
    val roomName: String = "",
    val status: String = "Available",
    val type: String = "Normal",
    val currentDate: String = "",
    val requests: Map<String, RoomRequest> = mapOf()
): Serializable

data class RoomRequest(
    val borrower: String = "",
    val status: String = "Available",
    val schedule: String = "",
    val role: String = "",
    val message: String = "",
    val endTime: Long = 0L
) : Serializable


