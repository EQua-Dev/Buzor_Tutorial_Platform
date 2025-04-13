package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

sealed class CreateCourseEvent {
    data class SubjectChanged(val subject: String) : CreateCourseEvent()
    data class TargetGradesChanged(val grade: String) : CreateCourseEvent()
    data class TitleChanged(val title: String) : CreateCourseEvent()
    data class DescriptionChanged(val description: String) : CreateCourseEvent()
    data class PriceChanged(val price: String) : CreateCourseEvent()
    data class CoverImageChanged(val uri: String) : CreateCourseEvent()
    object AddSection : CreateCourseEvent()
    data class SectionTitleChanged(val index: Int, val title: String) : CreateCourseEvent()
    data class SectionMaterialChanged(val index: Int, val materialUri: String) : CreateCourseEvent()
    data class SectionFootnoteChanged(val index: Int, val footnote: String) : CreateCourseEvent()
    data class RemoveSection(val index: Int) : CreateCourseEvent()
    data class PrivateSessionChanged(val allow: Boolean) : CreateCourseEvent()
    data class PrivateSessionPriceChanged(val price: String) : CreateCourseEvent()
    data class GroupSessionChanged(val allow: Boolean) : CreateCourseEvent()
    data class GroupSessionPriceChanged(val price: String) : CreateCourseEvent()
    data class GroupSessionDateChanged(val date: String) : CreateCourseEvent()
    object IncreaseMaxSeats : CreateCourseEvent()
    object DecreaseMaxSeats : CreateCourseEvent()
    object CreateCourse : CreateCourseEvent()
    object NextStep: CreateCourseEvent()
}