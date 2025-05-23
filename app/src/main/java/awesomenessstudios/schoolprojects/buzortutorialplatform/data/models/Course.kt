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
    val allowPrivateSessions: Boolean = true,
    val privateSessionPrice: String = "",
    val rating: Double = 0.0,
    val raters: Map<String, Int> = emptyMap(), // NEW: userId to rating (1–5)
    val courseNoteOneTitle: String = "",
    val courseNoteOne: String = "",
    val courseNoteOneFootnote: String = "",
    val courseNoteTwoTitle: String = "",
    val courseNoteTwo: String = "",
    val courseNoteTwoFootnote: String = "",
    val courseNoteThreeTitle: String = "",
    val courseNoteThree: String = "",
    val courseNoteThreeFootnote: String = "",
    val enrolledStudents: List<String> = listOf(),
    val isDeleted: Boolean = false,
    val dateCreated: String = ""
)
