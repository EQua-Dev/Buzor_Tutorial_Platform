package awesomenessstudios.schoolprojects.buzortutorialplatform.data

// Add this sealed class to your codebase (in a shared file)
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}