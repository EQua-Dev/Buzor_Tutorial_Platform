package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class Teacher(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val subjects: List<String> = listOf(),
    val rating: Double = 0.0,
    val isVerified: Boolean = false

)
