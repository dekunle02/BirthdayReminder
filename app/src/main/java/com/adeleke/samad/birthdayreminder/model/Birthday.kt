package com.adeleke.samad.birthdayreminder.model

import java.util.*

data class Birthday (
    var id: String = generateID(),
    val name: String = "Name",
    val dayOfBirth: String = "1",
    val monthOfBirth: String = "1",
    val yearOfBirth: String = "2000",
    val phoneNumber: String = "Phone number",
    val textMessage: String = " Happy Birthday!!!"
) {
    companion object{
        private const val prefix = "birthday- "
        private fun generateID(): String {
            return prefix + UUID.randomUUID().toString()
        }
    }
}
