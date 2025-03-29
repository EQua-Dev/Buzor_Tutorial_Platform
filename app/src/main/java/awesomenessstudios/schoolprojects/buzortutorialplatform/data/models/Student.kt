package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class Student(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val grade: String = "",
    val profileImage: String = "",
    val isVerified: Boolean = false
)