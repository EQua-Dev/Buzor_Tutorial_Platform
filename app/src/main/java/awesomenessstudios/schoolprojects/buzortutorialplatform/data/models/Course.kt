package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class Course(
    val id: String = "",
    val title: String = "",
    val ownerId: String = "",
    val price: String = "",
    val subject: String = "",
    val targetGrades: List<String> = listOf(),
    val coverImage: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val courseNoteOne: String = "",
    val courseNoteTwo: String = "",
    val courseNoteThree: String = "",
    val isDeleted: Boolean = false,
    val dateCreated: String = ""
)
