package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class Wallet(
    val id: String = "",
    val ownerId: String = "",
    val balance: String = "0.0",
    val dateCreated: String = "",
    val creationLocation: String = "",
    val securityQuestion1: String = "",
    val securityQuestion2: String = "",
    val hashType: String = "",
    val securityHash: String = "" // Hash of the security questions and answers

)
